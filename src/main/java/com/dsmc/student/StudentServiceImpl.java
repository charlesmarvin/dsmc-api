package com.dsmc.student;

import com.dsmc.student.domain.Student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

  private final StudentRepository studentRepository;

  @Autowired
  public StudentServiceImpl(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  @Override
  public List<Student> getStudents(String companyId, Pageable pageable) {
    return studentRepository.findByCompanyId(companyId, pageable).getContent();
  }

  @Override
  public Optional<Student> getStudent(String companyId, String studentId) {
    return Optional.ofNullable(studentRepository.findByCompanyIdAndId(companyId, studentId));
  }

  @Override
  public Optional<Student> createStudent(String companyId, Student student) {
    student.setId(null);
    student.setCompanyId(companyId);
    return updateStudent(companyId, null, student);
  }

  @Override
  public Optional<Student> updateStudent(String companyId, String studentId, Student student) {
    student.setId(studentId);
    student.setCompanyId(companyId);
    return Optional.ofNullable(studentRepository.save(student));
  }
}
