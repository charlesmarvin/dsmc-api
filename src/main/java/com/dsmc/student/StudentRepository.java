package com.dsmc.student;

import com.dsmc.student.domain.Student;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends MongoRepository<Student, String> {
  Page<Student> findByPrimaryPhoneLikeOrSecondaryPhoneLike(@Param("companyId") String companyId,
                                                           @Param("phone") String primaryPhone,
                                                           @Param("phone") String secondaryPhone,
                                                           Pageable pageable);

  default Page<Student> findByPhoneNumber(@Param("companyId") String companyId,
                                          @Param("phone") String phoneNumber,
                                          Pageable pageable) {
    return findByPrimaryPhoneLikeOrSecondaryPhoneLike(companyId, phoneNumber, phoneNumber, pageable);
  }

  Page<Student> findByCompanyId(@Param("companyId") String companyId, Pageable pageable);

  Student findByCompanyIdAndId(@Param("companyId") String companyId, @Param("id") String studentId);
}