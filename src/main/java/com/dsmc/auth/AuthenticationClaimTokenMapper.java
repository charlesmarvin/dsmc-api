package com.dsmc.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Service
class AuthenticationClaimTokenMapper {
  private final ObjectMapper objectMapper;
  private final TypeReference<Map<String, Object>> type = new TypeReference<Map<String, Object>>() {
  };

  @Autowired
  AuthenticationClaimTokenMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  Authentication fromClaims(Map<String, Object> claims) {
    Identity identity;
    try {
      String json = objectMapper.writeValueAsString(claims);
      identity = objectMapper.readValue(json, Identity.class);
    } catch (IOException e) {
      return null;
    }
    return new UsernamePasswordAuthenticationToken(identity.getUsername(), identity.getPassword(), Collections.EMPTY_LIST);
  }

  Map<String, Object> fromIdentity(Identity identity) {
    try {
      String json = objectMapper.writerWithView(Identity.View.Principal.class)
          .writeValueAsString(identity);
      return objectMapper.readValue(json, type);
    } catch (IOException e) {
      return null;
    }
  }
}
