package com.mt.access.domain.model;

public interface EncryptionService {
    String encryptedValue(String secret);

    boolean compare(String raw, String encrypted);
}
