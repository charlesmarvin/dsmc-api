package com.dsmc.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MultiTenantAuthenticationToken extends UsernamePasswordAuthenticationToken {
  private final String tenantId;
  private final String userId;

  public MultiTenantAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, Object userId, Object tenantId) {
    super(principal, credentials, authorities);
    this.userId = userId == null ? null : userId.toString();
    this.tenantId = tenantId == null ? null : tenantId.toString();
  }

  public String getTenantId() {
    return tenantId;
  }

  public String getUserId() {
    return userId;
  }
}
