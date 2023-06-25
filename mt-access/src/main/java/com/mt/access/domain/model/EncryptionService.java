package com.mt.access.domain.model;

import com.mt.access.domain.model.user.CurrentPassword;
import com.mt.access.domain.model.user.UserPassword;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface EncryptionService {
    String encryptedValue(String secret);

    boolean compare(UserPassword userPassword, CurrentPassword currentPwd);
}
