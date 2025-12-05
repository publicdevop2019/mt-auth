package com.mt.common.domain.model.job;

import com.mt.common.domain.model.local_transaction.TransactionContext;
import java.util.function.Consumer;

public interface DistributedJobService {
    void execute(String jobName, Consumer<TransactionContext> jobFn, boolean transactional);

    void resetLock(String jobName);
}
