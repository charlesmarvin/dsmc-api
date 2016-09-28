package com.dsmc.auth;

import com.dsmc.common.service.encryption.EncryptionService;
import com.dsmc.user.AdminService;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hibernate.validator.constraints.NotBlank;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("api/v1/auth")
@SuppressWarnings("unused")
public class AuthController {


  private final AdminService adminService;
  private final ModelMapper modelMapper;

  @Autowired
  public AuthController(AdminService adminService,
                        EncryptionService encryptionService,
                        ModelMapper modelMapper) {
    this.adminService = adminService;
    this.modelMapper = modelMapper;
  }

  @RequestMapping(method = RequestMethod.POST, path = "/token")
  public ResponseEntity<?> addCompany(@RequestBody TokenRequest tokenRequest) {

    return new ResponseEntity<>(HttpStatus.OK);
  }

  private static class TokenRequest {
    private String username;
    private String password;

    @JsonCreator
    public TokenRequest(@JsonProperty("username") @NotBlank String username,
                        @JsonProperty("password") @NotBlank String password) {
      this.username = username;
      this.password = password;
    }

    public String getUsername() {
      return username;
    }

    public String getPassword() {
      return password;
    }
  }
}
