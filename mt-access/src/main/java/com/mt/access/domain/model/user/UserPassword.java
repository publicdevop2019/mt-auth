package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
public class UserPassword {
    @Getter
    private String password;

    public UserPassword(String password) {
        setPassword(password);
    }

    private void setPassword(String rawPassword) {
        Validator.notNull(rawPassword);
        Validator.notBlank(rawPassword);
        Validator.lengthLessThanOrEqualTo(rawPassword, 16);
        Validator.lengthGreaterThanOrEqualTo(rawPassword, 10);
        Pattern p = Pattern.compile("[a-z]");
        Matcher m = p.matcher(rawPassword);
        if (!m.find()) {
            throw new IllegalArgumentException("at least one letter");
        }
        Pattern p2 = Pattern.compile("[0-9]");
        Matcher m2 = p2.matcher(rawPassword);
        if (!m2.find()) {
            throw new IllegalArgumentException("at least one number");
        }
        Pattern p3 = Pattern.compile("\\s");
        Matcher m3 = p3.matcher(rawPassword);
        if (m3.find()) {
            throw new IllegalArgumentException("no space allowed");
        }
        Pattern p4 = Pattern.compile("^(?=.*[~`!@#$%^&*()--+={}\\[\\]|\\\\:;\"'<>,.?/_₹]).*$");
        Matcher m4 = p4.matcher(rawPassword);
        if (!m4.find()) {
            throw new IllegalArgumentException("at least one special character");
        }
        this.password = DomainRegistry.getEncryptionService().encryptedValue(rawPassword);
    }
}
