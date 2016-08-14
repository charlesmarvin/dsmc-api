package com.dsmc.user.domain;

import com.dsmc.common.domain.Status;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class Company {
  @Id
  private String id;
  private String name;
  private Status status;
  @DBRef
  private User contact;
  private boolean verified;
  @CreatedDate
  private Date createdOn;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public User getContact() {
    return contact;
  }

  public void setContact(User contact) {
    this.contact = contact;
  }

  public boolean isVerified() {
    return verified;
  }

  public void setVerified(boolean verified) {
    this.verified = verified;
  }

  public Date getCreatedOn() {
    return createdOn;
  }
}
