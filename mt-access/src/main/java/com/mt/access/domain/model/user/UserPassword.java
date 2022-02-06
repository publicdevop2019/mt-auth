package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor
public class UserPassword {
    @Getter
    private String password;

    public UserPassword(String password) {
        setPassword(password);
    }

    private void setPassword(String password) {
        Validator.notNull(password);
        Validator.notBlank(password);
        this.password = DomainRegistry.getEncryptionService().encryptedValue(password);
    }
}
