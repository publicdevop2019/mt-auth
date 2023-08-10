package com.mt.access.domain.model.report;

import java.util.Set;

public interface RawAccessRecordRepository {

    Set<RawAccessRecord> getBucketRequestRecordSinceId(Long id);

    Set<RawAccessRecord> getResponseForUuid(Set<String> collect);

    void addAll(Set<RawAccessRecord> records);
}
