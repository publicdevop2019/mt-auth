package com.mt.access.application.sub_request;

import static com.mt.access.domain.model.audit.AuditActionName.APPROVE_SUB_REQUEST;
import static com.mt.access.domain.model.audit.AuditActionName.CANCEL_SUB_REQUEST;
import static com.mt.access.domain.model.audit.AuditActionName.CREATE_SUB_REQUEST;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_CACHE_PROFILE;
import static com.mt.access.domain.model.audit.AuditActionName.REJECT_SUB_REQUEST;
import static com.mt.access.domain.model.permission.Permission.SUB_REQ_MNGMT;

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
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
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
            .canAccess(tenantIds, SUB_REQ_MNGMT);
        SubRequestQuery subRequestQuery = new SubRequestQuery(queryParam, pageParam);
        return DomainRegistry.getSubRequestRepository()
            .getByQuery(subRequestQuery);
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
            .canAccess(tenantIds, SUB_REQ_MNGMT);
        SubRequestQuery subRequestQuery = SubRequestQuery.mySubscriptions(pageParam);
        return DomainRegistry.getSubRequestRepository().getMySubscriptions(subRequestQuery);
    }

    /**
     * get endpoint id that is subscribed
     *
     * @return unique endpoint ids
     */
    public Set<EndpointId> internalSubscribedEndpointIds() {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        return DomainRegistry.getSubRequestRepository().getSubscribeEndpointIds(userId);
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
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                EndpointId endpointId = new EndpointId(command.getEndpointId());
                Optional<Endpoint> endpoint =
                    DomainRegistry.getEndpointRepository().endpointOfId(endpointId);
                if (endpoint.isEmpty()) {
                    throw new DefinedRuntimeException("unable to find related endpoint", "0020",
                        HttpResponseCode.BAD_REQUEST,
                        ExceptionCatalog.ILLEGAL_ARGUMENT);
                }
                Endpoint endpoint1 = endpoint.get();
                ProjectId epProjectId = endpoint1.getProjectId();
                SubRequest subRequest = new SubRequest(
                    new ProjectId(command.getProjectId()),
                    endpointId,
                    command.getBurstCapacity(),
                    command.getReplenishRate(),
                    epProjectId,
                    endpoint1.isExpired(),
                    endpoint1.isAuthRequired(),
                    endpoint1.isShared()
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
        Optional<SubRequest> byId =
            DomainRegistry.getSubRequestRepository().getById(new SubRequestId(id));
        byId.ifPresent(ee -> {
            DomainRegistry.getPermissionCheckService().sameCreatedBy(ee);
            CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(changeId, (ignored) -> {
                    ee.update(command.getBurstCapacity(), command.getReplenishRate());
                    return null;
                }, SUB_REQUEST);
        });
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
            .idempotent(changeId, (ignored) -> {
                Optional<SubRequest> byId =
                    DomainRegistry.getSubRequestRepository().getById(subRequestId);
                byId.ifPresent(e -> {
                    DomainRegistry.getPermissionCheckService().sameCreatedBy(e);
                    DomainRegistry.getSubRequestRepository().remove(e);
                    DomainRegistry.getAuditService()
                        .storeAuditAction(CANCEL_SUB_REQUEST,
                            e);
                    DomainRegistry.getAuditService()
                        .logUserAction(log, CANCEL_SUB_REQUEST,
                            e);
                });
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
        Optional<SubRequest> byId = DomainRegistry.getSubRequestRepository().getById(subRequestId);
        byId.ifPresent(e -> {
            ProjectId endpointProjectId = e.getEndpointProjectId();
            DomainRegistry.getPermissionCheckService()
                .canAccess(endpointProjectId, SUB_REQ_MNGMT);
            CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(changeId, (ignored) -> {
                    UserId userId = DomainRegistry.getCurrentUserService().getUserId();
                    byId.ifPresent(e1 -> e1.approve(userId));
                    CommonDomainRegistry.getDomainEventRepository()
                        .append(new SubRequestApprovedEvent(e.getSubRequestId()));
                    return null;
                }, SUB_REQUEST);
        });
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
        Optional<SubRequest> byId = DomainRegistry.getSubRequestRepository().getById(subRequestId);
        byId.ifPresent(e -> {
            ProjectId endpointProjectId = e.getEndpointProjectId();
            DomainRegistry.getPermissionCheckService()
                .canAccess(endpointProjectId, SUB_REQ_MNGMT);
            CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(changeId, (ignored) -> {
                    UserId userId = DomainRegistry.getCurrentUserService().getUserId();
                    byId.ifPresent(reject -> reject.reject(command.getRejectionReason(), userId));
                    return null;
                }, SUB_REQUEST);
        });
    }

    /**
     * send bell notification to all endpoint subscriber
     *
     * @param event endpoint expired event
     */
    public void handle(EndpointExpired event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (ignored) -> {
                DomainId domainId = event.getDomainId();
                EndpointId endpointId = new EndpointId(domainId.getDomainId());
                Set<UserId> subscribers =
                    DomainRegistry.getSubRequestRepository().getEndpointSubscriber(endpointId);
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new SubscriberEndpointExpireEvent(endpointId, subscribers));
                return null;
            }, SUB_REQUEST);

    }
}
