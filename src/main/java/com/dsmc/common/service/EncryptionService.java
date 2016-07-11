package com.dsmc.common.service;

import com.dsmc.common.domain.QueryableSecureValue;
import com.dsmc.common.domain.SecureValue;

public interface EncryptionService {
    SecureValue encrypt(String value);

    QueryableSecureValue encryptQueryable(String value);

    String decrypt(SecureValue secureValue);

    byte[] hash(String value);
}
