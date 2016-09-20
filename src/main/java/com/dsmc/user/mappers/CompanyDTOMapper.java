package com.dsmc.user.mappers;

import com.dsmc.common.service.encryption.StringToQueryableSecureValueConverter;
import com.dsmc.user.domain.Company;
import com.dsmc.user.dto.CompanyDTO;

import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyDTOMapper extends PropertyMap<CompanyDTO, Company> {
  private final StringToQueryableSecureValueConverter stringToQueryableConverter;

  @Autowired
  public CompanyDTOMapper(StringToQueryableSecureValueConverter stringToQueryableConverter) {
    this.stringToQueryableConverter = stringToQueryableConverter;

  }

  @Override
  protected void configure() {
    map().setName(source.getName());
    using(stringToQueryableConverter).map(source.getContact().getEmail(), destination.getContact().getEmail());
    using(stringToQueryableConverter).map(source.getContact().getPhone(), destination.getContact().getPhone());
    using(stringToQueryableConverter).map(source.getContact().getFirstName(), destination.getContact().getFirstName());
    using(stringToQueryableConverter).map(source.getContact().getLastName(), destination.getContact().getLastName());
  }
}
