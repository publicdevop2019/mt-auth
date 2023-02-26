package com.mt.common.domain.model.domain_event;

import com.mt.common.infrastructure.RabbitMqEventStreamService;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public interface SagaEventStreamService extends EventStreamService {

    void replyOf(String subscribedApplicationName, boolean internal, String eventName,
                 Consumer<StoredEvent> consumer);

    void replyCancelOf(String subscribedApplicationName, boolean internal, String eventName,
                       Consumer<StoredEvent> consumer);

    void cancelOf(String subscribedApplicationName, boolean internal, String eventName,
                  Consumer<StoredEvent> consumer);

    void of(String subscribedApplicationName, boolean internal, String eventName,
            Consumer<StoredEvent> consumer);
}
