package com.mt.access.domain.model.temporary_code;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.AnyDomainId;
import java.util.Optional;

public interface TemporaryCodeRepository {
    Optional<TemporaryCode> query(String operationType, AnyDomainId domainId);

    Optional<TemporaryCode> queryNoneExpired(String operationType, AnyDomainId domainId,
                                             Integer expireInMilli);

    void add(ClientId clientId, TemporaryCode temporaryCode);

    void updateCode(ClientId clientId, AnyDomainId domainId,
                    String code);


}
