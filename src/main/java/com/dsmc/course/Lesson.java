package com.dsmc.course;

import com.dsmc.common.domain.Address;
import com.dsmc.instructor.Instructor;
import com.dsmc.student.domain.Student;
import com.dsmc.user.domain.Company;
import com.dsmc.user.domain.User;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Lesson {
  @Id
  private String id;
  @DBRef
  private Student student;
  @DBRef
  private Instructor instructor;
  private Integer priceOverride;
  private Integer paymentReceived;
  private LocalDateTime sessionDatetime;
  private Address address;
  private Company company;
  @CreatedDate
  private LocalDate createdOn;
  @CreatedBy
  private User createdBy;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Student getStudent() {
    return student;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public Instructor getInstructor() {
    return instructor;
  }

  public void setInstructor(Instructor instructor) {
    this.instructor = instructor;
  }

  public Integer getPriceOverride() {
    return priceOverride;
  }

  public void setPriceOverride(Integer priceOverride) {
    this.priceOverride = priceOverride;
  }

  public Integer getPaymentReceived() {
    return paymentReceived;
  }

  public void setPaymentReceived(Integer paymentReceived) {
    this.paymentReceived = paymentReceived;
  }

  public LocalDateTime getSessionDatetime() {
    return sessionDatetime;
  }

  public void setSessionDatetime(LocalDateTime sessionDatetime) {
    this.sessionDatetime = sessionDatetime;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public LocalDate getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(LocalDate createdOn) {
    this.createdOn = createdOn;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }
}
