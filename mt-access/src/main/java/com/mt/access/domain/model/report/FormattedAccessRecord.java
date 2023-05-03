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
    public static final String CLIENT_IP = "clientIp";
    public static final String ENDPOINT_ID_KEY = "endpointId";
    public static final String REQ_TIMESTAMP = "timestamp";
    public static final String PATH = "path";
    public static final String METHOD = "method";
    public static final String RESP_TIMESTAMP = "timestamp";
    public static final String STATUS_CODE = "statusCode";
    public static final String CONTENT_LENGTH = "contentLength";
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
        this.path = recordAsMap.get(PATH);
        this.requestAt =
            Date.from(Instant.ofEpochMilli(Long.parseLong(recordAsMap.get(REQ_TIMESTAMP))));
        this.method = recordAsMap.get(METHOD);
        this.endpointId = new EndpointId(recordAsMap.get(ENDPOINT_ID_KEY));
        setClientIp(recordAsMap.get(CLIENT_IP));
        Map<String, String> recordAsMap1 = response.getRecordAsMap();
        this.responseAt =
            Date.from(Instant.ofEpochMilli(Long.parseLong(recordAsMap1.get(RESP_TIMESTAMP))));
        this.responseCode = Integer.parseInt(recordAsMap1.get(STATUS_CODE));
        this.responseContentSize = Integer.parseInt(recordAsMap1.get(CONTENT_LENGTH));
    }

    private void setClientIp(String clientIp) {
        this.clientIp = clientIp.replace("_", ":");
    }
}
