package com.dsmc.auth;

import com.dsmc.user.UserRepository;
import com.dsmc.user.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DefaultUserService implements UserService {
  private final UserRepository userRepository;

  @Autowired
  public DefaultUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("Unknown User");
    }
    return new AuthUserDetails(user);
  }
}
