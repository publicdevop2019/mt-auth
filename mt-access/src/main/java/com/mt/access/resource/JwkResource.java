package com.mt.access.resource;

import com.mt.access.infrastructure.JwtInfoProviderService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwkResource {

    @Autowired
    private JwtInfoProviderService jwtInfoProviderService;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        return jwtInfoProviderService.getPublicKeys().toJSONObject();
    }
}
