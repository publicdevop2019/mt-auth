package com.mt.common.domain.model.idempotent;

import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;

public interface ChangeRecordRepository {
    Optional<ChangeRecord> internalQuery(String changeId, String entityType);

    void add(ChangeRecord changeRecord);
}
