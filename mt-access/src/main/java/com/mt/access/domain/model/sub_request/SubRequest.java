package com.mt.access.domain.model.sub_request;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table
@Slf4j
@NoArgsConstructor
@Getter
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "subRequestRegion")
@NamedQuery(name = "getMySubscriptions", query = "SELECT en FROM SubRequest as en WHERE en.createdBy = :createdBy AND en.subRequestStatus = 'APPROVED' GROUP BY en.endpointId HAVING MAX(en.modifiedAt) > 0")
@NamedQuery(name = "getMySubscriptionsCount", query = "SELECT COUNT(*) FROM SubRequest sr WHERE sr.id IN (SELECT en.id FROM SubRequest as en WHERE en.createdBy = :createdBy AND en.subRequestStatus = 'APPROVED' GROUP BY en.endpointId HAVING MAX(en.modifiedAt) > 0)")
@NamedQuery(name = "getEpSubscriptions", query = "SELECT en FROM SubRequest as en WHERE en.endpointId IN :endpointIds AND en.subRequestStatus = 'APPROVED' GROUP BY en.endpointId HAVING MAX(en.modifiedAt) > 0")
@NamedQuery(name = "getEpSubscriptionsCount", query = "SELECT COUNT(*) FROM SubRequest sr WHERE sr.id IN (SELECT en.id FROM SubRequest as en WHERE en.endpointId IN :endpointIds AND en.subRequestStatus = 'APPROVED' GROUP BY en.endpointId HAVING MAX(en.modifiedAt) > 0)")
public class SubRequest extends Auditable {
    @Enumerated(EnumType.STRING)
    private SubRequestStatus subRequestStatus = SubRequestStatus.PENDING;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "projectId"))
    })
    private ProjectId projectId;
    @Embedded
    private SubRequestId subRequestId;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "endpointId"))
    })
    private EndpointId endpointId;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "endpointProjectId"))
    })
    private ProjectId endpointProjectId;
    private Integer replenishRate;
    private Integer burstCapacity;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "approvedBy"))
    })
    private UserId approvedBy;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "rejectedBy"))
    })
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
        setReplenishRate(replenishRate);
        setBurstCapacity(burstCapacity);
        this.endpointProjectId = endpointProjectId;
        if (!secured) {
            //public endpoints use default based rate from endpoint owner
            setPublicEndpointReplenishRate();
            setPublicEndpointBurstCapacity();
        }
        new SubRequestValidator(this, new HttpValidationNotificationHandler()).validate();
    }

    private void setPublicEndpointBurstCapacity() {
        this.burstCapacity = 0;
    }

    private void setPublicEndpointReplenishRate() {
        replenishRate = 0;
    }


    public void update(Integer replenishRate, Integer burstCapacity) {
        setReplenishRate(replenishRate);
        setBurstCapacity(burstCapacity);
        new SubRequestValidator(this, new HttpValidationNotificationHandler()).validate();
    }

    public void approve(UserId userId) {
        this.subRequestStatus = SubRequestStatus.APPROVED;
        this.approvedBy = userId;
    }

    public void reject(String rejectionReason, UserId userId) {
        this.rejectionBy = userId;
        setRejectionReason(rejectionReason);
        this.subRequestStatus = SubRequestStatus.REJECTED;
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
}
