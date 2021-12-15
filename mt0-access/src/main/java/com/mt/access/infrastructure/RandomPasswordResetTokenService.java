package com.mt.access.infrastructure;

import com.mt.access.domain.model.user.PasswordResetTokenService;
import org.springframework.stereotype.Service;

@Service
public class RandomPasswordResetTokenService implements PasswordResetTokenService {
    @Override
    public String generate() {
        return "123456789";
//        return UUID.randomUUID().toString().replace("-", "");
    }
}
