package com.dsmc.user.dto;

import com.dsmc.common.domain.Status;

public class CompanyDTO {
  private String id;
  private String name;
  private Status status;
  private UserDTO contact;

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

  public UserDTO getContact() {
    return contact;
  }

  public void setContact(UserDTO contact) {
    this.contact = contact;
  }
}
