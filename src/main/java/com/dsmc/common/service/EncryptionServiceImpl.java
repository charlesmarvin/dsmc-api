package com.dsmc.common.service;

import com.dsmc.common.domain.QueryableSecureValue;
import com.dsmc.common.domain.SecureValue;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.util.Base64;

@Service
public class EncryptionServiceImpl implements EncryptionService {
    private final SecretKeySpec secretKeySpec;
    private final String salt;
    private final Cipher encryptionCipher;
    private final Cipher decryptionCipher;
    private final Mac hasher;

    @Autowired
    public EncryptionServiceImpl(@Value("${app.security.encryption.key}") String key,
                                 @Value("${app.security.encryption.salt}") String salt,
                                 @Value("${app.security.encryption.algo}") String algo
    ) {
        this.salt = salt;
        try {
            Security.addProvider(new BouncyCastleProvider());
            secretKeySpec = new SecretKeySpec(key.getBytes(), algo);
            encryptionCipher = Cipher.getInstance(algo);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            decryptionCipher = Cipher.getInstance(algo);
            decryptionCipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            hasher = Mac.getInstance("PBEWithHmacSHA1");
            hasher.init(secretKeySpec);
        } catch (Exception e) {
            throw new RuntimeException("Initialization failed.", e);
        }
    }

    @Override
    public SecureValue encrypt(String value) {
        if (StringUtils.isEmpty(value)) return null;
        try {
            return new SecureValue(encryptionCipher.doFinal(value.getBytes()));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public QueryableSecureValue encryptQueryable(String value) {
        if (StringUtils.isEmpty(value)) return null;
        try {
            byte[] hash = hasher.doFinal((salt + value).getBytes());
            return new QueryableSecureValue(encryptionCipher.doFinal(value.getBytes()), hash);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decrypt(SecureValue secureValue) {
        if (secureValue == null || secureValue.getValue() == null) {
            return null;
        }
        try {
            return new String(decryptionCipher.doFinal(secureValue.getValue()));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] hash(String value) {
        if (value == null) return null;
        return hasher.doFinal((salt + value).getBytes());
    }

    @Override
    public String hashBase64(String value) {
        return new String(Base64.getEncoder().encode(hash(value)));
    }
}
