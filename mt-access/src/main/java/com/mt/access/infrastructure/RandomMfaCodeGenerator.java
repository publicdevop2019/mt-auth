package com.mt.access.infrastructure;

import com.mt.access.domain.model.user.MfaCodeGenerator;
import org.springframework.stereotype.Service;

@Service
public class RandomMfaCodeGenerator implements MfaCodeGenerator {
    @Override
    public String generate() {
        // for testing
        // int m = (int) Math.pow(10, 6 - 1);
        // return String.valueOf(m + new Random().nextInt(9 * m));
        return "654321";
    }
}
