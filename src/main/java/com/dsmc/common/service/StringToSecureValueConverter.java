package com.dsmc.common.service;

import com.dsmc.common.domain.SecureValue;

import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StringToSecureValueConverter extends AbstractConverter<String, SecureValue> {
  private final EncryptionService encryptionService;

  @Autowired
  public StringToSecureValueConverter(EncryptionService encryptionService) {
    this.encryptionService = encryptionService;
  }

  @Override
  protected SecureValue convert(String source) {
    return encryptionService.encryptQueryable(source);
  }
}
