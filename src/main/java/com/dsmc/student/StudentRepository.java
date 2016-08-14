package com.dsmc.student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends MongoRepository<Student, String> {
  Page<Student> findByPrimaryPhoneLikeOrSecondaryPhoneLike(@Param("phone") String primaryPhone, @Param("phone") String secondaryPhone, Pageable pageable);

  default Page<Student> findByPhoneNumber(@Param("phone") String phoneNumber, Pageable pageable) {
    return findByPrimaryPhoneLikeOrSecondaryPhoneLike(phoneNumber, phoneNumber, pageable);
  }
}