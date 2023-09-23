package com.mt.access.domain.model.report;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import java.util.Map;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
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
    @Getter(AccessLevel.PUBLIC)
    protected Long id;
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "domainId", column = @Column(name = "endpoint_id"))
    })
    private EndpointId endpointId;
    private Long requestAt;

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
    private Long responseAt;
    private Integer responseCode;
    private Integer responseContentSize;


    public FormattedAccessRecord(RawAccessRecord request, RawAccessRecord response) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        Map<String, String> recordAsMap = request.getRecordAsMap();
        this.path = recordAsMap.get(PATH);
        this.requestAt = Long.parseLong(recordAsMap.get(REQ_TIMESTAMP));
        this.method = recordAsMap.get(METHOD);
        this.endpointId = new EndpointId(recordAsMap.get(ENDPOINT_ID_KEY));
        setClientIp(recordAsMap.get(CLIENT_IP));
        Map<String, String> recordAsMap1 = response.getRecordAsMap();
        this.responseAt = Long.parseLong(recordAsMap1.get(RESP_TIMESTAMP));
        this.responseCode = Integer.parseInt(recordAsMap1.get(STATUS_CODE));
        this.responseContentSize = Integer.parseInt(recordAsMap1.get(CONTENT_LENGTH));
    }

    public static FormattedAccessRecord fromDatabaseRow(Long id, EndpointId endpointId,
                                                        Long requestAt,
                                                        String path, String clientIp, UserId userId,
                                                        ProjectId projectId, String method,
                                                        Long responseAt, Integer responseCode,
                                                        Integer responseContentSize) {
        FormattedAccessRecord record = new FormattedAccessRecord();
        record.setId(id);
        record.setClientIp(clientIp);
        record.endpointId = endpointId;
        record.requestAt = requestAt;
        record.path = path;
        record.userId = userId;
        record.projectId = projectId;
        record.method = method;
        record.responseAt = responseAt;
        record.responseCode = responseCode;
        record.responseContentSize = responseContentSize;
        return record;
    }

    private void setClientIp(String clientIp) {
        this.clientIp = clientIp.replace("_", ":");
    }
}
