package com.dsmc.user;

import com.dsmc.user.domain.Verification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

public interface VerificationRepository extends MongoRepository<Verification, String> {
    Verification findByIdentifier(@Param("identifier") String identifier);
}
