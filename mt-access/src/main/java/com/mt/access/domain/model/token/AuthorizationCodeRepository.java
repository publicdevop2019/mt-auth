package com.mt.access.domain.model.token;

public interface AuthorizationCodeRepository {
    void store(String code, AuthorizeInfo authorizeInfo);

    AuthorizeInfo remove(String code);
}
