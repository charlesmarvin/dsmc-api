package com.dsmc.common.service;

import com.dsmc.common.domain.QueryableSecureValue;
import com.dsmc.common.domain.SecureValue;
import com.dsmc.common.service.encryption.EncryptionService;
import com.dsmc.common.service.encryption.EncryptionServiceImpl;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class EncryptionServiceImplTest {
    private static final String ALGO = "AES";
    private static String SALT = "8XdU6BDHlwPaay8IyzyCmqr7R4lWCg7B1jJBT9xKruYKo3ZN5cglzqOFOpFHxrB7";
    private static String KEY = "Wy871XfvqCp9rTKG";

    private static EncryptionService encryptionService;

    @BeforeClass
    public static void init() {
        encryptionService = new EncryptionServiceImpl(KEY, SALT, ALGO);
    }


    @Test
    public void testEncryptDecrypt() throws Exception {
        String value = "this is some value that needs to be encrypted";
        SecureValue secureValue = encryptionService.encrypt(value);
        String decryptedValue = encryptionService.decrypt(secureValue);

        assertNotNull("should return non-null SecureValue", secureValue);
        assertNotEquals("should perform encryption", value, new String(secureValue.getValue()));
        assertEquals("should decrypt to original encrypted value", value, decryptedValue);
    }

    @Test
    public void testEncryptNull() throws Exception {
        SecureValue secureValue = encryptionService.encrypt(null);
        assertNull("should return null SecureValue", secureValue);

        QueryableSecureValue queryableSecureValue = encryptionService.encryptQueryable(null);
        assertNull("should return null SecureValue", queryableSecureValue);
    }

    @Test
    public void testDecryptNull() throws Exception {
        String value = encryptionService.decrypt(null);
        assertNull("should return null value", value);
    }

    @Test
    public void testEncryptBlank() throws Exception {
        SecureValue secureValue = encryptionService.encrypt("");
        assertNull("should return null SecureValue", secureValue);

        QueryableSecureValue queryableSecureValue = encryptionService.encryptQueryable("");
        assertNull("should return null SecureValue", queryableSecureValue);
    }

    @Test
    public void testHash() throws Exception {
        String value = "searchable secure value";
        QueryableSecureValue secureValue1 = encryptionService.encryptQueryable(value);
        QueryableSecureValue secureValue2 = encryptionService.encryptQueryable(value);
        assertNotNull("should return non-null SecureValue", secureValue1);
        assertArrayEquals("should has values consistently", secureValue1.getHash(), secureValue2.getHash());

    }

}