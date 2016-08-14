package com.dsmc.user.mappers;

import com.dsmc.common.service.QueryableSecureValueToStringConverter;
import com.dsmc.user.domain.User;
import com.dsmc.user.dto.UserDTO;

import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserMapper extends PropertyMap<User, UserDTO> {
  private final QueryableSecureValueToStringConverter queryableSecureValueToStringConverter;

  @Autowired
  public UserMapper(QueryableSecureValueToStringConverter queryableSecureValueToStringConverter) {
    this.queryableSecureValueToStringConverter = queryableSecureValueToStringConverter;

  }

  @Override
  protected void configure() {
    using(queryableSecureValueToStringConverter).map(source.getEmail(), destination.getEmail());
    using(queryableSecureValueToStringConverter).map(source.getPhone(), destination.getPhone());
    using(queryableSecureValueToStringConverter).map(source.getFirstName(), destination.getFirstName());
    using(queryableSecureValueToStringConverter).map(source.getLastName(), destination.getLastName());
  }
}