package com.mt.access.domain.model.email_delivery;

import java.util.Optional;

public interface EmailDeliveryRepository {
    Optional<EmailDelivery> getEmailDelivery(String deliverTo, BizType bizType);
}
