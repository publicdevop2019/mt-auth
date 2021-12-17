package com.mt.access.resource;

import com.mt.access.infrastructure.JwtInfoProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class JWKResource {

    @Autowired
    private JwtInfoProviderService jwtInfoProviderService;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        return jwtInfoProviderService.getPublicKeys().toJSONObject();
    }
}
