package com.mt.access.domain.model.temporary_code;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TemporaryCodeService {
    public void verifyCode(String rawCode, Integer expireAfterMilli, String operationType,
                           String rawDomainId) {
        AnyDomainId domainId = new AnyDomainId(rawDomainId);
        Optional<TemporaryCode> query =
            DomainRegistry.getTemporaryCodeRepository()
                .queryNoneExpired(operationType, domainId, expireAfterMilli);
        if (query.isEmpty()) {
            throw new DefinedRuntimeException("code not exist", "1003",
                HttpResponseCode.BAD_REQUEST);
        }
        if (!query.get().getCode().equals(rawCode)) {
            throw new DefinedRuntimeException("code mismatch", "1004",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public void issueCode(ClientId clientId, String rawCode, String operationType,
                          String rawDomainId) {
        AnyDomainId domainId = new AnyDomainId(rawDomainId);
        Optional<TemporaryCode> query =
            DomainRegistry.getTemporaryCodeRepository().query(operationType, domainId);
        if (query.isEmpty()) {
            DomainRegistry.getTemporaryCodeRepository()
                .add(clientId, new TemporaryCode(domainId, rawCode, operationType));
        } else {
            DomainRegistry.getTemporaryCodeRepository()
                .updateCode(clientId, domainId, rawCode);
        }
    }
}
