package com.dsmc.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

class AuthenticatedUser implements Authentication {
  private final AuthUserDetails details;
  private boolean authenticated = true;

  AuthenticatedUser(Identity identity) {
    details = new AuthUserDetails(identity);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getDetails() {
    return details;
  }

  @Override
  public Object getPrincipal() {
    return null;
  }

  @Override
  public boolean isAuthenticated() {
    return authenticated;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    authenticated = isAuthenticated;
  }

  @Override
  public String getName() {
    return details.getUsername();
  }
}
