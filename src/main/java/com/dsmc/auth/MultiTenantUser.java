package com.dsmc.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class MultiTenantUser extends User {
  private final String identifier;
  private final String tenantId;

  public MultiTenantUser(String username,
                         String password,
                         Collection<? extends GrantedAuthority> authorities,
                         String identifier,
                         String tenantId) {
    super(username, password, authorities);
    this.identifier = identifier;
    this.tenantId = tenantId;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getTenantId() {
    return tenantId;
  }
}
