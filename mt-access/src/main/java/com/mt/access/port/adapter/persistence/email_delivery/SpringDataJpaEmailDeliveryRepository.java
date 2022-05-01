package com.mt.access.port.adapter.persistence.email_delivery;

import com.mt.access.domain.model.email_delivery.BizType;
import com.mt.access.domain.model.email_delivery.EmailDelivery;
import com.mt.access.domain.model.email_delivery.EmailDeliveryRepository;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 * use pessimistic lock to prevent code from executing, due to third party api can not be undo.
 */
public interface SpringDataJpaEmailDeliveryRepository extends JpaRepository<EmailDelivery, Long>,
    EmailDeliveryRepository {
    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("SELECT p FROM #{#entityName} as p WHERE p.deliverTo = ?1 AND p.bizType = ?2")
    Optional<EmailDelivery> findByDeliverToAndBizType(String deliverTo, BizType bizType);

    default Optional<EmailDelivery> getEmailDelivery(String deliverTo, BizType bizType) {
        return findByDeliverToAndBizType(deliverTo, bizType);
    }
}
