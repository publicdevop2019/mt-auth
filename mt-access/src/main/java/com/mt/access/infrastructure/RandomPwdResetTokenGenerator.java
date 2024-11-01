package com.mt.access.infrastructure;

import com.mt.access.domain.model.user.PwdResetTokenGenerator;
import org.springframework.stereotype.Service;

@Service
public class RandomPwdResetTokenGenerator implements PwdResetTokenGenerator {
    @Override
    public String generate() {
        //return UUID.randomUUID().toString().replace("-", "");
        return "123456789";
    }
}
