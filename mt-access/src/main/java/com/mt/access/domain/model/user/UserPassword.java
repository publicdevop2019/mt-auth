package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
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
            throw new DefinedRuntimeException("at least one letter", "0064",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        Pattern p2 = Pattern.compile("[0-9]");
        Matcher m2 = p2.matcher(rawPassword);
        if (!m2.find()) {
            throw new DefinedRuntimeException("at least one number", "0065",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        Pattern p3 = Pattern.compile("\\s");
        Matcher m3 = p3.matcher(rawPassword);
        if (m3.find()) {
            throw new DefinedRuntimeException("no space allowed", "0066",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        Pattern p4 = Pattern.compile("^(?=.*[~`!@#$%^&*()--+={}\\[\\]|\\\\:;\"'<>,.?/_₹]).*$");
        Matcher m4 = p4.matcher(rawPassword);
        if (!m4.find()) {
            throw new DefinedRuntimeException("at least one special character", "0067",
                HttpResponseCode.BAD_REQUEST,
                ExceptionCatalog.ILLEGAL_ARGUMENT);
        }
        this.password = DomainRegistry.getEncryptionService().encryptedValue(rawPassword);
    }
}
