package com.mt.access.domain.model.report;

import static com.mt.access.domain.model.report.FormattedAccessRecord.ENDPOINT_ID_KEY;

import com.mt.common.domain.CommonDomainRegistry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name",
    "instanceId", "recordId"}))
@Slf4j
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class RawAccessRecord {
    @Id
    @Setter(AccessLevel.PROTECTED)
    protected Long id;
    private String name;
    private Boolean isRequest;
    private Boolean isResponse;
    private Boolean processed = Boolean.FALSE;
    private String uuid;
    private String instanceId;
    private String recordId;
    private String record;

    public RawAccessRecord(String name, String instanceId, String record) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.name = name;
        this.instanceId = instanceId;
        this.record = record;
        Map<String, String> recordAsMap = getRecordAsMap();
        this.recordId = recordAsMap.get("id");
        this.isRequest = "request".equalsIgnoreCase(recordAsMap.get("type"));
        this.isResponse = "response".equalsIgnoreCase(recordAsMap.get("type"));
        this.uuid = recordAsMap.get("uuid");
    }

    public static RawAccessRecord fromDatabaseRow(Long id, String name, String instanceId,
                                                  String recordId, String record, Boolean isRequest,
                                                  Boolean processed, Boolean isResponse,
                                                  String uuid) {
        RawAccessRecord rawAccessRecord = new RawAccessRecord();
        rawAccessRecord.setId(id);
        rawAccessRecord.name = name;
        rawAccessRecord.instanceId = instanceId;
        rawAccessRecord.recordId = recordId;
        rawAccessRecord.record = record;
        rawAccessRecord.isRequest = isRequest;
        rawAccessRecord.isResponse = isResponse;
        rawAccessRecord.uuid = uuid;
        rawAccessRecord.processed = processed;
        return rawAccessRecord;
    }

    public Map<String, String> getRecordAsMap() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        String[] split = record.split(",");
        Arrays.stream(split).forEach(str -> {
            if (str.split(":").length != 2) {
                log.error("invalid record format {}", str);
            } else {
                stringStringHashMap.put(str.split(":")[0], str.split(":")[1]);
            }
        });
        return stringStringHashMap;
    }

    public void markAsProcessed() {
        this.processed = true;
    }

    public boolean endpointNotFound() {
        return "not_found".equalsIgnoreCase(this.getRecordAsMap().get(ENDPOINT_ID_KEY));
    }
}
