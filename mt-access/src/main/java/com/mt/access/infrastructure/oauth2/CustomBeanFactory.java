package com.mt.access.infrastructure.oauth2;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.stereotype.Component;

/**
 * config BeanFactory.
 */
@Slf4j
@Component
public class CustomBeanFactory {
    private static final Integer STRENGTH = 12;

    @Bean
    private KeyPair keyPair(Environment env) {
        KeyStoreKeyFactory keyStoreKeyFactory =
            new KeyStoreKeyFactory(
                new ClassPathResource(
                    Objects.requireNonNull(env.getProperty("mt.jwt.key-store"))),
                Objects.requireNonNull(env.getProperty("mt.jwt.password"))
                    .toCharArray());

        return keyStoreKeyFactory.getKeyPair(env.getProperty("mt.jwt.alias"));
    }

    @Bean
    private JWKSet jwkSet(KeyPair keyPair) {
        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
            .keyUse(KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.RS256)
            .keyID("manytree-id");
        return new JWKSet(builder.build());
    }

    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(STRENGTH);
    }
}
