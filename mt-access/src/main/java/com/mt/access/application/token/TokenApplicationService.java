package com.mt.access.application.token;

import com.mt.access.domain.DomainRegistry;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TokenApplicationService {
    public ResponseEntity<?> grantToken(String clientId, String clientSecret,
                                        Map<String, String> parameters, String agentInfo,
                                        String clientIpAddress, String changeId) {
        return DomainRegistry.getTokenGrantService()
            .grantToken(clientId, clientSecret, parameters, agentInfo, clientIpAddress, changeId);
    }

    public String authorize(Map<String, String> parameters) {
        return DomainRegistry.getTokenGrantService().authorize(parameters);
    }
}
