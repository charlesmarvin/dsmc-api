package com.dsmc.user;

import com.dsmc.user.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    User findByUsername(@Param("username") String username);

    @Query(value="{ 'company.id' : ?0 }")
    List<User> findAllByCompany(String companyId);

    @Query(value="{ 'company.id': ?0, 'id': ?1 }")
    User findOneByCompanyAndUser(String companyId, String userId);
}
