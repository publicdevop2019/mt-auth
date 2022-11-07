package com.mt.access.domain.model.sub_request;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Where;

@Entity
@Table
@Slf4j
@NoArgsConstructor
@Getter
@Where(clause = "deleted=0")
public class SubRequest extends Auditable {
    private Status status = Status.PENDING;
    @Embedded
    private ProjectId projectId;
    @Embedded
    private SubRequestId subRequestId;
    @Embedded
    private EndpointId endpointId;
    @Embedded
    private ProjectId endpointProjectId;
    private int maxInvokePerSec;
    private int maxInvokePerMin;
    @Embedded
    private UserId approvedBy;
    @Embedded
    private UserId rejectionBy;
    private String rejectionReason;


    public SubRequest(ProjectId projectId,
                      EndpointId endpointId,
                      int maxInvokePerSec,
                      int maxInvokePerMin,
                      ProjectId endpointProjectId
    ) {
        this.subRequestId = new SubRequestId();
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.projectId = projectId;
        this.endpointId = endpointId;
        this.maxInvokePerSec = maxInvokePerSec;
        this.maxInvokePerMin = maxInvokePerMin;
        this.endpointProjectId = endpointProjectId;
    }

    public void update(int maxInvokePerSec, int maxInvokePerMin) {
        this.maxInvokePerSec = maxInvokePerSec;
        this.maxInvokePerMin = maxInvokePerMin;
    }

    public void approve(UserId userId) {
        this.status = Status.APPROVED;
        this.approvedBy = userId;
    }

    public void cancel() {
        this.status = Status.CANCELLED;
        softDelete();
    }

    public void reject(String rejectionReason, UserId userId) {
        this.rejectionBy = userId;
        this.rejectionReason = rejectionReason;
    }
}
