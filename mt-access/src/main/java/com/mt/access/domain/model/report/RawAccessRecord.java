package com.mt.access.domain.model.report;

import com.mt.common.domain.CommonDomainRegistry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
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
public class RawAccessRecord {
    @Id
    @Setter(AccessLevel.PROTECTED)
    protected Long id;
    private String name;
    private boolean isRequest;
    private boolean isResponse;
    private boolean processed;
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

    public Map<String, String> getRecordAsMap() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        String[] split = record.split(",");
        Arrays.stream(split).forEach(str -> {
            stringStringHashMap.put(str.split(":")[0], str.split(":")[1]);
        });
        return stringStringHashMap;
    }

    public void markAsProcessed() {
        this.processed = true;
    }
}
