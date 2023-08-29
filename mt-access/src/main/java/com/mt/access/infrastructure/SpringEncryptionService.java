package com.mt.access.infrastructure;

import com.mt.access.domain.model.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SpringEncryptionService implements EncryptionService {

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public boolean compare(String raw, String encrypted) {
        return encoder.matches(raw, encrypted);
    }

    public String encryptedValue(String secret) {
        return encoder.encode(secret);
    }

}
