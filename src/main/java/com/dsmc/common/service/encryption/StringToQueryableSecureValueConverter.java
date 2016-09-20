package com.dsmc.common.service.encryption;

import com.dsmc.common.domain.QueryableSecureValue;

import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StringToQueryableSecureValueConverter extends AbstractConverter<String, QueryableSecureValue> {
  private final EncryptionService encryptionService;

  @Autowired
  public StringToQueryableSecureValueConverter(EncryptionService encryptionService) {
    this.encryptionService = encryptionService;
  }

  @Override
  protected QueryableSecureValue convert(String source) {
    return encryptionService.encryptQueryable(source);
  }
}
