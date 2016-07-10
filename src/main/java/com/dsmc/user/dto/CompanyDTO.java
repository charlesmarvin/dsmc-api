package com.dsmc.user.dto;

import com.dsmc.common.Status;

/**
 * Created by charlesmarvin on 7/9/16.
 */
public class CompanyDTO {
    private String id;
    private String name;
    private Status status;

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
}
