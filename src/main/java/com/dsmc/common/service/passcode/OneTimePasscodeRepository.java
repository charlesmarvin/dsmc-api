package com.dsmc.common.service.passcode;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

public interface OneTimePasscodeRepository extends MongoRepository<OneTimePasscode, String> {
  OneTimePasscode findByIdentifier(@Param("identifier") String identifier);
}
