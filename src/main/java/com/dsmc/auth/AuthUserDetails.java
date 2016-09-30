package com.dsmc.auth;

import com.dsmc.common.domain.Status;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

class AuthUserDetails implements UserDetails {
  private final Identity user;

  AuthUserDetails(Identity user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return user.getStatus() != Status.Locked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return user.getStatus() == Status.Active;
  }
}
