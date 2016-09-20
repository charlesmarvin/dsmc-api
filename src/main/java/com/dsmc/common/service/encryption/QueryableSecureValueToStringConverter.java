package com.dsmc.common.service.encryption;

import com.dsmc.common.domain.QueryableSecureValue;

import org.modelmapper.AbstractConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QueryableSecureValueToStringConverter extends AbstractConverter<QueryableSecureValue, String> {
  private final EncryptionService encryptionService;

  @Autowired
  public QueryableSecureValueToStringConverter(EncryptionService encryptionService) {
    this.encryptionService = encryptionService;
  }

  @Override
  protected String convert(QueryableSecureValue source) {
    return encryptionService.decrypt(source);
  }
}
