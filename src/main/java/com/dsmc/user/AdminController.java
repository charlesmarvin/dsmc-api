package com.dsmc.user;

import com.dsmc.common.service.encryption.EncryptionService;
import com.dsmc.user.domain.Company;
import com.dsmc.user.dto.CompanyDTO;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
@RequestMapping("api/v1/admin")
@SuppressWarnings("unused")
public class AdminController {


  private final AdminService adminService;
  private final ModelMapper modelMapper;

  @Autowired
  public AdminController(AdminService adminService,
                         EncryptionService encryptionService,
                         ModelMapper modelMapper) {
    this.adminService = adminService;
    this.modelMapper = modelMapper;
  }

  @RequestMapping(method = RequestMethod.POST, path = "/companies")
  public ResponseEntity<?> addCompany(@RequestBody CompanyDTO companyDTO) {
    Company company = adminService.createCompany(modelMapper.map(companyDTO, Company.class));
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setLocation(ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(company.getId())
        .toUri());
    return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
  }

  @RequestMapping(method = RequestMethod.PUT, path = "/company/{companyId}")
  public ResponseEntity<?> updateCompany(@RequestParam("companyId") String companyId,
                                         @RequestBody CompanyDTO companyDTO) {
    companyDTO.setId(companyId);
    adminService.updateCompany(modelMapper.map(companyDTO, Company.class));
    return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
  }

  @RequestMapping(method = RequestMethod.GET, path = "/companies")
  public List<CompanyDTO> getCompanies(@RequestParam(value = "page", required = false) Integer page,
                                       @RequestParam(value = "pageSize", required = false) Integer pageSize) {
    return adminService.getCompanies(page, pageSize)
        .stream()
        .map(company -> modelMapper.map(company, CompanyDTO.class))
        .collect(Collectors.toList());
  }



}
