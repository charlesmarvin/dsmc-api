package com.dsmc.common.service.encryption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DefaultPasswordEncoder implements PasswordEncoder {
  private final EncryptionService encryptionService;

  @Autowired
  public DefaultPasswordEncoder(EncryptionService encryptionService) {
    this.encryptionService = encryptionService;
  }

  @Override
  public String encode(CharSequence rawPassword) {
    return encryptionService.hashBase64(rawPassword.toString());
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return encode(rawPassword).equals(encodedPassword);
  }
}
