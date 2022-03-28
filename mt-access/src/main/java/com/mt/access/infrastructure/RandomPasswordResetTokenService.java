package com.mt.access.infrastructure;

import com.mt.access.domain.model.user.PasswordResetTokenService;
import org.springframework.stereotype.Service;

@Service
public class RandomPasswordResetTokenService implements PasswordResetTokenService {
    @Override
    public String generate() {
        //return UUID.randomUUID().toString().replace("-", "");
        return "123456789";
    }
}
