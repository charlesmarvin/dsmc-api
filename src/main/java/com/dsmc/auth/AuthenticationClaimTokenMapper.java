package com.dsmc.auth;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthenticationClaimTokenMapper {
  public Authentication fromClaims(Map<String, Object> claims) {
    return new AuthenticatedUser((String) claims.get("sub"));
  }

}
