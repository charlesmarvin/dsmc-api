package com.dsmc.user;

import com.dsmc.user.domain.Company;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CompanyRepository extends MongoRepository<Company, String> {
  @Query(value = "{ 'company.email': ?0 }")
  Company findByEmail(String email);
}
