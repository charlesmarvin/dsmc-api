package com.dsmc.student;

import com.dsmc.student.domain.Student;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudentService {
  List<Student> getStudents(String companyId, Pageable pageable);

  Optional<Student> getStudent(String companyId, String studentId);

  Optional<Student> createStudent(String companyId, Student student);

  Optional<Student> updateStudent(String companyId, String studentId, Student student);
}
