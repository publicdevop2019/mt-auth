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
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.sub_request.SubRequest;
import com.mt.access.domain.model.sub_request.SubRequestId;
import com.mt.access.domain.model.sub_request.SubRequestQuery;
import com.mt.access.domain.model.sub_request.event.SubRequestApprovedEvent;
import com.mt.access.domain.model.sub_request.event.SubscribedEndpointExpired;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.access.domain.model.user.UserRelationQuery;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Set;
import java.util.stream.Collectors;
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
        ProjectId id = DomainRegistry.getCurrentUserService().getViewProjectId();
        DomainRegistry.getPermissionCheckService()
            .canAccess(id, SUB_REQ_MGMT);
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
        ProjectId id = DomainRegistry.getCurrentUserService().getViewProjectId();
        DomainRegistry.getPermissionCheckService()
            .canAccess(id, SUB_REQ_MGMT);
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
        SubRequest sub =
            DomainRegistry.getSubRequestRepository().get(new SubRequestId(id));
        DomainRegistry.getPermissionCheckService().canAccess(sub.getProjectId(), SUB_REQ_MGMT);
        DomainRegistry.getPermissionCheckService().sameCreatedBy(sub);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                SubRequest update =
                    sub.update(command.getReplenishRate(), command.getBurstCapacity());
                DomainRegistry.getSubRequestRepository().update(sub, update);
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
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                SubRequest subRequest = DomainRegistry.getSubRequestRepository().get(subRequestId);
                ProjectId endpointProjectId = subRequest.getEndpointProjectId();
                DomainRegistry.getPermissionCheckService()
                    .canAccess(endpointProjectId, SUB_REQ_MGMT);
                UserId userId = DomainRegistry.getCurrentUserService().getUserId();
                SubRequest approve = subRequest.approve(userId);
                context
                    .append(new SubRequestApprovedEvent(subRequest.getSubRequestId()));
                DomainRegistry.getSubRequestRepository().update(subRequest, approve);
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
                SubRequest reject = subRequest.reject(command.getRejectionReason(), userId);
                DomainRegistry.getSubRequestRepository().update(subRequest, reject);
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
                Set<ProjectId> projectsSubscribed =
                    DomainRegistry.getSubRequestRepository().getSubProjectId(endpointId);
                if (!projectsSubscribed.isEmpty()) {
                    //find project current admins
                    //@note it could be very slow due to N+1 issue
                    Set<UserId> allAdmins = projectsSubscribed.stream().flatMap(tenantProjectId -> {
                        RoleId tenantAdminRoleId =
                            DomainRegistry.getRoleRepository()
                                .query(RoleQuery.tenantAdmin(tenantProjectId))
                                .findFirst().get().getRoleId();
                        Set<UserRelation> allByQuery = QueryUtility.getAllByQuery(
                            (q) -> DomainRegistry.getUserRelationRepository()
                                .query(q),
                            UserRelationQuery.internalAdminQuery(tenantAdminRoleId));
                        return allByQuery.stream().map(UserRelation::getUserId);
                    }).collect(Collectors.toSet());
                    context
                        .append(new SubscribedEndpointExpired(endpointId, allAdmins));
                } else {
                    log.debug("skip sending SubscriberEndpointExpireEvent due to not subscribed");
                }
                return null;
            }, SUB_REQUEST);

    }
}
