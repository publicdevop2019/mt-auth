package com.mt.access.application.sub_request;

import static com.mt.access.domain.model.audit.AuditActionName.APPROVE_SUB_REQUEST;
import static com.mt.access.domain.model.audit.AuditActionName.CANCEL_SUB_REQUEST;
import static com.mt.access.domain.model.audit.AuditActionName.CREATE_SUB_REQUEST;
import static com.mt.access.domain.model.audit.AuditActionName.REJECT_SUB_REQUEST;
import static com.mt.access.domain.model.permission.Permission.SUB_REQ_MGMT;

import com.mt.access.application.sub_request.command.CreateSubRequestCommand;
import com.mt.access.application.sub_request.command.RejectSubRequestCommand;
import com.mt.access.application.sub_request.command.UpdateSubRequestCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.event.EndpointExpired;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.sub_request.SubRequest;
import com.mt.access.domain.model.sub_request.SubRequestId;
import com.mt.access.domain.model.sub_request.SubRequestQuery;
import com.mt.access.domain.model.sub_request.event.SubRequestApprovedEvent;
import com.mt.access.domain.model.sub_request.event.SubscriberEndpointExpireEvent;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SubRequestApplicationService {

    private static final String SUB_REQUEST = "SUB_REQUEST";

    /**
     * query subscription request based on given params
     *
     * @param queryParam query info
     * @param pageParam  page info
     * @return paginated data
     */
    public SumPagedRep<SubRequest> query(String queryParam, String pageParam) {
        Set<ProjectId> tenantIds = DomainRegistry.getCurrentUserService().getTenantIds();
        DomainRegistry.getPermissionCheckService()
            .canAccess(tenantIds, SUB_REQ_MGMT);
        SubRequestQuery subRequestQuery = new SubRequestQuery(queryParam, pageParam);
        return DomainRegistry.getSubRequestRepository()
            .query(subRequestQuery);
    }

    /**
     * get subscriptions with pagination
     *
     * @param pageParam page info
     * @return paginated data
     */
    public SumPagedRep<SubRequest> query(String pageParam) {
        Set<ProjectId> tenantIds = DomainRegistry.getCurrentUserService().getTenantIds();
        DomainRegistry.getPermissionCheckService()
            .canAccess(tenantIds, SUB_REQ_MGMT);
        SubRequestQuery subRequestQuery = SubRequestQuery.mySubscriptions(pageParam);
        return DomainRegistry.getSubRequestRepository().getMySubscriptions(subRequestQuery);
    }

    /**
     * get endpoint id that is subscribed
     *
     * @return unique endpoint ids
     */
    public Set<EndpointId> internalSubscribedEndpointIds(ProjectId projectId) {
        return DomainRegistry.getSubRequestRepository().getSubscribeEndpointIds(projectId);
    }

    /**
     * create sub request
     *
     * @param command  create command
     * @param changeId unique change id
     * @return created sub request domain id
     */
    @AuditLog(actionName = CREATE_SUB_REQUEST)
    public String create(CreateSubRequestCommand command, String changeId) {
        ProjectId projectId = new ProjectId(command.getProjectId());
        DomainRegistry.getPermissionCheckService().canAccess(projectId, SUB_REQ_MGMT);
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                EndpointId endpointId = new EndpointId(command.getEndpointId());
                Endpoint endpoint =
                    DomainRegistry.getEndpointRepository().get(endpointId);
                ProjectId epProjectId = endpoint.getProjectId();
                SubRequest subRequest = new SubRequest(
                    projectId,
                    endpointId,
                    command.getReplenishRate(),
                    command.getBurstCapacity(),
                    epProjectId,
                    endpoint.getExpired(),
                    endpoint.getSecured(),
                    endpoint.getShared()
                );
                DomainRegistry.getSubRequestRepository().add(subRequest);
                return subRequest.getSubRequestId().getDomainId();
            }, SUB_REQUEST);
    }

    /**
     * update sub request.
     *
     * @param id       sub request id
     * @param command  update command
     * @param changeId unique change id
     */
    public void update(String id, UpdateSubRequestCommand command, String changeId) {
        SubRequest byId =
            DomainRegistry.getSubRequestRepository().get(new SubRequestId(id));
        DomainRegistry.getPermissionCheckService().canAccess(byId.getProjectId(), SUB_REQ_MGMT);
        DomainRegistry.getPermissionCheckService().sameCreatedBy(byId);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                byId.update(command.getReplenishRate(), command.getBurstCapacity());
                return null;
            }, SUB_REQUEST);
    }

    /**
     * cancel sub request.
     *
     * @param id       sub request id
     * @param changeId unique change id
     */
    @AuditLog(actionName = CANCEL_SUB_REQUEST)
    public void cancel(String id, String changeId) {
        SubRequestId subRequestId = new SubRequestId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                SubRequest subRequest =
                    DomainRegistry.getSubRequestRepository().get(subRequestId);
                DomainRegistry.getPermissionCheckService().sameCreatedBy(subRequest);
                DomainRegistry.getSubRequestRepository().remove(subRequest);
                DomainRegistry.getAuditService()
                    .storeAuditAction(CANCEL_SUB_REQUEST,
                        subRequest);
                DomainRegistry.getAuditService()
                    .logUserAction(log, CANCEL_SUB_REQUEST,
                        subRequest);
                return null;
            }, SUB_REQUEST);
    }

    /**
     * approve sub request.
     *
     * @param id       sub request id
     * @param changeId unique change id
     */
    @AuditLog(actionName = APPROVE_SUB_REQUEST)
    public void approve(String id, String changeId) {
        SubRequestId subRequestId = new SubRequestId(id);
        SubRequest subRequest = DomainRegistry.getSubRequestRepository().get(subRequestId);
        ProjectId endpointProjectId = subRequest.getEndpointProjectId();
        DomainRegistry.getPermissionCheckService()
            .canAccess(endpointProjectId, SUB_REQ_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                UserId userId = DomainRegistry.getCurrentUserService().getUserId();
                subRequest.approve(userId);
                context
                    .append(new SubRequestApprovedEvent(subRequest.getSubRequestId()));
                return null;
            }, SUB_REQUEST);
    }

    /**
     * reject sub request.
     *
     * @param id       sub request id
     * @param command  reject command
     * @param changeId unique change id
     */
    @AuditLog(actionName = REJECT_SUB_REQUEST)
    public void reject(String id, RejectSubRequestCommand command, String changeId) {
        SubRequestId subRequestId = new SubRequestId(id);
        SubRequest subRequest = DomainRegistry.getSubRequestRepository().get(subRequestId);
        ProjectId endpointProjectId = subRequest.getEndpointProjectId();
        DomainRegistry.getPermissionCheckService()
            .canAccess(endpointProjectId, SUB_REQ_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                UserId userId = DomainRegistry.getCurrentUserService().getUserId();
                subRequest.reject(command.getRejectionReason(), userId);
                return null;
            }, SUB_REQUEST);
    }

    /**
     * send bell notification to all endpoint subscriber
     *
     * @param event endpoint expired event
     */
    public void handle(EndpointExpired event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                DomainId domainId = event.getDomainId();
                EndpointId endpointId = new EndpointId(domainId.getDomainId());
                Set<UserId> subscribers =
                    DomainRegistry.getSubRequestRepository().getEndpointSubscriber(endpointId);
                if (!subscribers.isEmpty()) {
                    context
                        .append(new SubscriberEndpointExpireEvent(endpointId, subscribers));
                } else {
                    log.debug("skip sending SubscriberEndpointExpireEvent due to not subscribed");
                }
                return null;
            }, SUB_REQUEST);

    }
}
