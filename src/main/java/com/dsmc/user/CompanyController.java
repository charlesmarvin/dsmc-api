package com.dsmc.user;

import com.dsmc.common.service.encryption.EncryptionService;
import com.dsmc.user.domain.Company;
import com.dsmc.user.domain.User;
import com.dsmc.user.dto.CompanyDTO;
import com.dsmc.user.dto.CompanyVerificationDTO;
import com.dsmc.user.dto.UserDTO;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;


@Controller
@ResponseBody
@RequestMapping("api/")
@PreAuthorize("#companyId == authentication.tenantId")
@SuppressWarnings("unused")
public class CompanyController {

  private final AdminService adminService;
  private final ModelMapper modelMapper;

  @Autowired
  public CompanyController(AdminService adminService,
                           EncryptionService encryptionService,
                           ModelMapper modelMapper) {
    this.adminService = adminService;
    this.modelMapper = modelMapper;
  }

  @PreAuthorize("permitAll")
  @RequestMapping(method = RequestMethod.POST, path = "/company/verify")
  public ResponseEntity<?> verifyCompany(@RequestBody CompanyVerificationDTO companyVerificationDTO) {
    boolean verified = adminService.verifyCompanyAccountByEmail(companyVerificationDTO.getIdentifier(),
        companyVerificationDTO.getVerificationCode());
    return new ResponseEntity<>(verified ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
  }

  @PreAuthorize("permitAll")
  @RequestMapping(method = RequestMethod.POST, path = "/company/verify/code")
  public ResponseEntity<?> resendCompanyVerificationCode(@RequestBody String email) {
    boolean verified = adminService.resendCompanyAccountVerificationByEmail(email);
    return new ResponseEntity<>(verified ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
  }

  @RequestMapping(method = RequestMethod.GET, path = "/companies/{companyId}")
  public CompanyDTO getCompanyById(@PathVariable("companyId") String companyId) {
    return modelMapper.map(adminService.getCompanyById(companyId), CompanyDTO.class);
  }

  @RequestMapping(method = RequestMethod.PUT, path = "/companies/{companyId}")
  public ResponseEntity<?> updateCompany(@RequestParam("companyId") String companyId,
                                         @RequestBody CompanyDTO companyDTO) {
    companyDTO.setId(companyId);
    adminService.updateCompany(modelMapper.map(companyDTO, Company.class));
    return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
  }

  @RequestMapping(method = RequestMethod.GET, path = "/companies/{companyId}/users")
  public List<UserDTO> getCompanyUsers(@PathVariable("companyId") String companyId) {
    return adminService.getCompanyUsers(companyId)
        .stream()
        .map(user -> modelMapper.map(user, UserDTO.class))
        .collect(Collectors.toList());
  }

  @RequestMapping(method = RequestMethod.GET, path = "/companies/{companyId}/users/{userId}")
  public UserDTO getCompanyUser(@PathVariable("companyId") String companyId,
                                @PathVariable("userId") String userId) {

    return modelMapper.map(adminService.getCompanyUser(companyId, userId), UserDTO.class);
  }

  @RequestMapping(method = RequestMethod.POST, path = "/companies/{companyId}/users")
  public ResponseEntity<?> addCompanyUser(@PathVariable("companyId") String companyId,
                                          @RequestBody UserDTO newUser) {
    User user = adminService.createCompanyUser(companyId, modelMapper.map(newUser, User.class));
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(user.getId())
        .toUri());
    return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
  }

  @RequestMapping(method = RequestMethod.PUT, path = "/companies/{companyId}/users/{userId}")
  public ResponseEntity<?> updateCompanyUser(@PathVariable("companyId") String companyId,
                                             @PathVariable("userId") String userId,
                                             @RequestBody UserDTO newUser) {
    newUser.setId(userId);
    adminService.updateCompanyUser(companyId, modelMapper.map(newUser, User.class));
    return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
  }
}
