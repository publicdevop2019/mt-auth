package com.mt.access.infrastructure;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

@Service
public class JwtInfoProviderService {

    /**
     * use Resource annotation to solve invoked before spring load issue
     */
    @Resource
    private Environment env;

    @Autowired
    private KeyPair keyPair;

    @Autowired
    private JWKSet jwkSet;

    @Bean
    private KeyPair keyPair() {

        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource(Objects.requireNonNull(env.getProperty("jwt.key-store"))), Objects.requireNonNull(env.getProperty("jwt.password")).toCharArray());

        return keyStoreKeyFactory.getKeyPair(env.getProperty("jwt.alias"));
    }

    @Bean
    private JWKSet jwkSet() {
        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID("manytree-id");
        return new JWKSet(builder.build());
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public JWKSet getPublicKeys() {
        return jwkSet;
    }
}
