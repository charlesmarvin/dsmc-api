package com.dsmc.common.service.encryption;

import com.dsmc.common.domain.SecureValue;

import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecureValueToStringConverter extends AbstractConverter<SecureValue, String> {
  private final EncryptionService encryptionService;

  @Autowired
  public SecureValueToStringConverter(EncryptionService encryptionService) {
    this.encryptionService = encryptionService;
  }

  @Override
  protected String convert(SecureValue source) {
    return encryptionService.decrypt(source);
  }
}
