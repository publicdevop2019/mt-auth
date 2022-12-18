package com.mt.access.domain.model.report;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Slf4j
@NoArgsConstructor
@Getter
public class FormattedAccessRecord {
    @Id
    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PRIVATE)
    protected Long id;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "endpoint_id"))
    })
    private EndpointId endpointId;
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestAt;

    private String path;
    private String clientIp;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "user_id"))
    })
    private UserId userId;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "project_id"))
    })
    private ProjectId projectId;
    private String method;
    @Temporal(TemporalType.TIMESTAMP)
    private Date responseAt;
    private int responseCode;
    private int responseContentSize;


    public FormattedAccessRecord(RawAccessRecord request, RawAccessRecord response) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        Map<String, String> recordAsMap = request.getRecordAsMap();
        this.path = recordAsMap.get("path");
        this.requestAt =
            Date.from(Instant.ofEpochSecond(Long.parseLong(recordAsMap.get("timestamp"))));
        this.method = recordAsMap.get("method");
        this.endpointId = new EndpointId(recordAsMap.get("endpointId"));
        this.clientIp = recordAsMap.get("clientIp");
        Map<String, String> recordAsMap1 = response.getRecordAsMap();
        this.responseAt =
            Date.from(Instant.ofEpochSecond(Long.parseLong(recordAsMap1.get("timestamp"))));
        this.responseCode = Integer.parseInt(recordAsMap1.get("statusCode"));
        this.responseContentSize = Integer.parseInt(recordAsMap1.get("contentLength"));
    }

}
