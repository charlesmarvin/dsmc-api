package com.dsmc.user.mappers;

import com.dsmc.common.service.QueryableSecureValueToStringConverter;
import com.dsmc.user.domain.Company;
import com.dsmc.user.dto.CompanyDTO;

import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyMapper extends PropertyMap<Company, CompanyDTO> {
  private final QueryableSecureValueToStringConverter queryableSecureValueToStringConverter;

  @Autowired
  public CompanyMapper(QueryableSecureValueToStringConverter queryableSecureValueToStringConverter) {
    this.queryableSecureValueToStringConverter = queryableSecureValueToStringConverter;

  }

  @Override
  protected void configure() {
    map().setName(source.getName());
    using(queryableSecureValueToStringConverter).map(source.getContact().getEmail(), destination.getContact().getEmail());
    using(queryableSecureValueToStringConverter).map(source.getContact().getPhone(), destination.getContact().getPhone());
    using(queryableSecureValueToStringConverter).map(source.getContact().getFirstName(), destination.getContact().getFirstName());
    using(queryableSecureValueToStringConverter).map(source.getContact().getLastName(), destination.getContact().getLastName());
  }
}
