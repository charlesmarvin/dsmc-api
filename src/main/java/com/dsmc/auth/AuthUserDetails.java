package com.dsmc.auth;

import java.util.Collections;

class AuthUserDetails extends org.springframework.security.core.userdetails.User {
  private final Identity identity;

  AuthUserDetails(Identity identity) {
    super(identity.getUsername(), identity.getPassword(), Collections.EMPTY_LIST);
    this.identity = identity;
  }

}
