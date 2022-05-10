package com.mt.access.infrastructure;

import com.mt.access.domain.model.user.MfaCodeService;
import org.springframework.stereotype.Service;

@Service
public class RandomMfaCodeService implements MfaCodeService {
    @Override
    public String generate() {
        // for testing
        // int m = (int) Math.pow(10, 6 - 1);
        // return String.valueOf(m + new Random().nextInt(9 * m));
        return "654321";
    }
}
