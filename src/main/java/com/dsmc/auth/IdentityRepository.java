package com.dsmc.auth;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

public interface IdentityRepository extends MongoRepository<Identity, String> {
  Identity findByUsername(@Param("username") String username);

  Identity findByIdentifier(@Param("identifier") String identifier);
}
