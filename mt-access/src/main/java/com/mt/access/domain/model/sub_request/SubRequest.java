package com.mt.access.domain.model.sub_request;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

@Entity
@Table
@Slf4j
@NoArgsConstructor
@Getter
@Where(clause = "deleted=0")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "subRequestRegion")
@NamedQuery(name = "getMySubscriptions", query = "SELECT en FROM SubRequest as en WHERE en.createdBy = :createdBy AND en.subRequestStatus = 'APPROVED' GROUP BY en.endpointId HAVING MAX(en.modifiedAt) > 0")
@NamedQuery(name = "getMySubscriptionsCount", query = "SELECT COUNT(*) FROM SubRequest sr WHERE sr.id IN (SELECT en.id FROM SubRequest as en WHERE en.createdBy = :createdBy AND en.subRequestStatus = 'APPROVED' GROUP BY en.endpointId HAVING MAX(en.modifiedAt) > 0)")
@NamedQuery(name = "getEpSubscriptions", query = "SELECT en FROM SubRequest as en WHERE en.endpointId IN :endpointIds AND en.subRequestStatus = 'APPROVED' GROUP BY en.endpointId HAVING MAX(en.modifiedAt) > 0")
@NamedQuery(name = "getEpSubscriptionsCount", query = "SELECT COUNT(*) FROM SubRequest sr WHERE sr.id IN (SELECT en.id FROM SubRequest as en WHERE en.endpointId IN :endpointIds AND en.subRequestStatus = 'APPROVED' GROUP BY en.endpointId HAVING MAX(en.modifiedAt) > 0)")
public class SubRequest extends Auditable {
    @Convert(converter = SubRequestStatus.DbConverter.class)
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
    private int replenishRate;
    private int burstCapacity;
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
                      int replenishRate,
                      int burstCapacity,
                      ProjectId endpointProjectId
    ) {
        this.subRequestId = new SubRequestId();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.projectId = projectId;
        this.endpointId = endpointId;
        this.replenishRate = replenishRate;
        this.burstCapacity = burstCapacity;
        this.endpointProjectId = endpointProjectId;
    }

    public void update(int replenishRate, int burstCapacity) {
        this.replenishRate = replenishRate;
        this.burstCapacity = burstCapacity;
    }

    public void approve(UserId userId) {
        this.subRequestStatus = SubRequestStatus.APPROVED;
        this.approvedBy = userId;
    }

    public void cancel() {
        this.subRequestStatus = SubRequestStatus.CANCELLED;
        softDelete();
    }

    public void reject(String rejectionReason, UserId userId) {
        this.rejectionBy = userId;
        this.rejectionReason = rejectionReason;
        this.subRequestStatus = SubRequestStatus.REJECTED;
    }
}
