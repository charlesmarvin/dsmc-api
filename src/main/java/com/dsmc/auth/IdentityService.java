package com.dsmc.auth;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface IdentityService extends UserDetailsService {
  Identity findByIdentifier(String identifier);

  void create(Identity identity);

  void update(Identity identity);
}
