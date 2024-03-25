package com.mt.access.domain.model.sub_request;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.time.Instant;
import java.util.Objects;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@Getter
public class SubRequest extends Auditable {
    private SubRequestStatus subRequestStatus = SubRequestStatus.PENDING;
    private ProjectId projectId;
    private SubRequestId subRequestId;
    private EndpointId endpointId;
    private ProjectId endpointProjectId;
    private Integer replenishRate;
    private Integer burstCapacity;
    private UserId approvedBy;
    private UserId rejectionBy;
    private String rejectionReason;


    public SubRequest(ProjectId projectId,
                      EndpointId endpointId,
                      Integer replenishRate,
                      Integer burstCapacity,
                      ProjectId endpointProjectId,
                      Boolean expired, Boolean secured, Boolean shared) {
        if (projectId.equals(endpointProjectId)) {
            throw new DefinedRuntimeException("cannot subscribe to itself", "1057",
                HttpResponseCode.BAD_REQUEST);
        }
        if (expired) {
            throw new DefinedRuntimeException("cannot subscribe to expired endpoint", "1058",
                HttpResponseCode.BAD_REQUEST);
        }
        if (!shared) {
            throw new DefinedRuntimeException("cannot subscribe to endpoint that is not shared",
                "1059",
                HttpResponseCode.BAD_REQUEST);
        }
        this.subRequestId = new SubRequestId();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.projectId = projectId;
        this.endpointId = endpointId;

        this.endpointProjectId = endpointProjectId;
        if (secured) {
            setReplenishRate(replenishRate);
            setBurstCapacity(burstCapacity);
        } else {
            //public endpoints use default based rate from endpoint owner
            setPublicEndpointReplenishRate();
            setPublicEndpointBurstCapacity();
        }
        new SubRequestValidator(this, new HttpValidationNotificationHandler()).validate();
        long milli = Instant.now().toEpochMilli();
        this.setCreatedAt(milli);
        this.setCreatedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
        this.setModifiedAt(milli);
        this.setModifiedBy(DomainRegistry.getCurrentUserService().getUserId().getDomainId());
    }

    public static SubRequest fromDatabaseRow(Long id, Long createdAt, String createdBy,
                                             Long modifiedAt, String modifiedBy, Integer version,
                                             SubRequestId domainId, Integer replenishRate,
                                             Integer burstCapacity, UserId approvedBy,
                                             UserId rejectedBy, ProjectId endpointProjectId,
                                             String rejectionReason,
                                             SubRequestStatus subRequestStatus,
                                             EndpointId endpointId, ProjectId projectId) {
        SubRequest subRequest = new SubRequest();
        subRequest.setId(id);
        subRequest.setCreatedAt(createdAt);
        subRequest.setCreatedBy(createdBy);
        subRequest.setModifiedAt(modifiedAt);
        subRequest.setModifiedBy(modifiedBy);
        subRequest.setVersion(version);
        subRequest.subRequestId = domainId;
        subRequest.replenishRate = replenishRate;
        subRequest.burstCapacity = burstCapacity;
        subRequest.approvedBy = approvedBy;
        subRequest.rejectionBy = rejectedBy;
        subRequest.endpointProjectId = endpointProjectId;
        subRequest.rejectionReason = rejectionReason;
        subRequest.subRequestStatus = subRequestStatus;
        subRequest.endpointId = endpointId;
        subRequest.projectId = projectId;
        return subRequest;
    }

    private void setPublicEndpointBurstCapacity() {
        this.burstCapacity = 0;
    }

    private void setPublicEndpointReplenishRate() {
        replenishRate = 0;
    }


    public SubRequest update(Integer replenishRate, Integer burstCapacity) {
        SubRequest subRequest =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        subRequest.setReplenishRate(replenishRate);
        subRequest.setBurstCapacity(burstCapacity);
        new SubRequestValidator(subRequest, new HttpValidationNotificationHandler()).validate();
        return subRequest;
    }

    public SubRequest approve(UserId userId) {
        SubRequest subRequest =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        subRequest.subRequestStatus = SubRequestStatus.APPROVED;
        subRequest.approvedBy = userId;
        return subRequest;
    }

    public SubRequest reject(String rejectionReason, UserId userId) {
        SubRequest subRequest =
            CommonDomainRegistry.getCustomObjectSerializer().nativeDeepCopy(this);
        this.rejectionBy = userId;
        setRejectionReason(rejectionReason);
        this.subRequestStatus = SubRequestStatus.REJECTED;
        return subRequest;
    }

    private void setRejectionReason(String rejectionReason) {
        Validator.validRequiredString(1, 50, rejectionReason);
        this.rejectionReason = rejectionReason;
    }

    private void setReplenishRate(Integer replenishRate) {
        Validator.notNull(replenishRate);
        Validator.greaterThanOrEqualTo(replenishRate, 1);
        Validator.lessThanOrEqualTo(replenishRate, 1000);
        this.replenishRate = replenishRate;
    }

    private void setBurstCapacity(Integer burstCapacity) {
        Validator.notNull(burstCapacity);
        Validator.greaterThanOrEqualTo(burstCapacity, 1);
        Validator.lessThanOrEqualTo(burstCapacity, 1500);
        this.burstCapacity = burstCapacity;
    }

    public boolean sameAs(SubRequest o) {
        return subRequestStatus == o.subRequestStatus &&
            Objects.equals(projectId, o.projectId) &&
            Objects.equals(subRequestId, o.subRequestId) &&
            Objects.equals(endpointId, o.endpointId) &&
            Objects.equals(endpointProjectId, o.endpointProjectId) &&
            Objects.equals(replenishRate, o.replenishRate) &&
            Objects.equals(burstCapacity, o.burstCapacity) &&
            Objects.equals(approvedBy, o.approvedBy) &&
            Objects.equals(rejectionBy, o.rejectionBy) &&
            Objects.equals(rejectionReason, o.rejectionReason);
    }

}
