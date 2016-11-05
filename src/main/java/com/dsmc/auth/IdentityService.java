package com.dsmc.auth;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface IdentityService extends UserDetailsService {
  Optional<Identity> findByIdentifier(String identifier);

  Optional<Identity> findByUsername(String username);

  void create(Identity identity);

  void update(Identity identity);
}
