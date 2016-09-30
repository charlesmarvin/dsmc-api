package com.dsmc.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DefaultIdentityService implements IdentityService {

  private final IdentityRepository identityRepository;

  @Autowired
  public DefaultIdentityService(IdentityRepository identityRepository) {
    this.identityRepository = identityRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Identity user = identityRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("Unknown User");
    }
    return new AuthUserDetails(user);
  }

  @Override
  public Identity findByIdentifier(String identifier) {
    return identityRepository.findByIdentifier(identifier);
  }

  @Override
  public void create(Identity identity) {
    identityRepository.insert(identity);
  }

  @Override
  public void update(Identity identity) {
    identityRepository.save(identity);
  }
}
