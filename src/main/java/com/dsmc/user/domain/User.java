package com.dsmc.user.domain;

import com.dsmc.common.domain.QueryableSecureValue;
import com.dsmc.common.domain.Status;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class User {
  @Id
  private String id;
  private String username;
  private String password;
  private QueryableSecureValue firstName;
  private QueryableSecureValue lastName;
  private QueryableSecureValue email;
  private QueryableSecureValue phone;
  private Status status;
  @DBRef
  private Company company;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public QueryableSecureValue getFirstName() {
    return firstName;
  }

  public void setFirstName(QueryableSecureValue firstName) {
    this.firstName = firstName;
  }

  public QueryableSecureValue getLastName() {
    return lastName;
  }

  public void setLastName(QueryableSecureValue lastName) {
    this.lastName = lastName;
  }

  public QueryableSecureValue getEmail() {
    return email;
  }

  public void setEmail(QueryableSecureValue email) {
    this.email = email;
  }

  public QueryableSecureValue getPhone() {
    return phone;
  }

  public void setPhone(QueryableSecureValue phone) {
    this.phone = phone;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
}
