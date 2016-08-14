package com.dsmc.user.mappers;

import com.dsmc.common.service.StringToQueryableSecureValueConverter;
import com.dsmc.user.domain.User;
import com.dsmc.user.dto.UserDTO;

import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserDTOMapper extends PropertyMap<UserDTO, User> {

  private final StringToQueryableSecureValueConverter stringToQueryableConverter;

  @Autowired
  public UserDTOMapper(StringToQueryableSecureValueConverter stringToQueryableConverter) {
    this.stringToQueryableConverter = stringToQueryableConverter;

  }

  @Override
  protected void configure() {
    using(stringToQueryableConverter).map(source.getEmail(), destination.getEmail());
    using(stringToQueryableConverter).map(source.getPhone(), destination.getPhone());
    using(stringToQueryableConverter).map(source.getFirstName(), destination.getFirstName());
    using(stringToQueryableConverter).map(source.getLastName(), destination.getLastName());
  }
}