package com.dsmc.user;

import com.dsmc.user.domain.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends MongoRepository<Company, String> {
    Company findByEmail(@Param("email") String email);
}
