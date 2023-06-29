package com.mt.access.infrastructure;

import com.mt.access.domain.model.EncryptionService;
import com.mt.access.domain.model.user.CurrentPassword;
import com.mt.access.domain.model.user.UserPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SpringEncryptionService implements EncryptionService {

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public boolean compare(UserPassword userPassword, CurrentPassword currentPwd) {
        return encoder.matches(currentPwd.getRawPassword(), userPassword.getPassword());
    }

    public String encryptedValue(String secret) {
        return encoder.encode(secret);
    }

}
