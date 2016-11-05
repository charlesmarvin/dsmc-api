package com.dsmc.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

class AuthUserDetails implements UserDetails {
  private final Identity identity;

  AuthUserDetails(Identity user) {
    Objects.requireNonNull(user);
    this.identity = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getPassword() {
    return identity.getPassword();
  }

  @Override
  public String getUsername() {
    return identity.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public String getCompanyId() {
    return identity.getCompanyId();
  }
}
