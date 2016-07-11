package com.dsmc.user;

import com.dsmc.common.service.EncryptionService;
import com.dsmc.user.domain.Company;
import com.dsmc.user.domain.User;
import com.dsmc.user.dto.CompanyDTO;
import com.dsmc.user.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("api/v1/admin")
@SuppressWarnings("unused")
public class AdminController {


    private final AdminService adminService;
    private final EncryptionService encryptionService;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminController(AdminService adminService,
                           EncryptionService encryptionService,
                           ModelMapper modelMapper) {
        this.adminService = adminService;
        this.encryptionService = encryptionService;
        this.modelMapper = modelMapper;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/companies")
    public ResponseEntity<?> addCompany(@RequestBody CompanyDTO companyDTO) {
        Company company = adminService.createCompany(fromDto(companyDTO));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(company.getId())
                .toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/companies")
    public List<CompanyDTO> getCompanies(@RequestParam(value = "page", required = false) Integer page,
                                         @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return adminService.getCompanies(page, pageSize)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/companies/{companyId}")
    public CompanyDTO getCompanyById(@PathVariable("companyId") String companyId) {
        return toDto(adminService.getCompanyById(companyId));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/companies/{companyId}/users")
    public List<UserDTO> getCompanyUsers(@PathVariable("companyId") String companyId) {
        return adminService.getCompanyUsers(companyId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/companies/{companyId}/users/{userId}")
    public UserDTO getCompanyUser(@PathVariable("companyId") String companyId,
                                  @PathVariable("userId") String userId) {

        return toDto(adminService.getCompanyUser(companyId, userId));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/companies/{companyId}/users")
    public ResponseEntity<?> addCompanyUser(@PathVariable("companyId") String companyId,
                                            @RequestBody UserDTO newUser) {
        User user = adminService.createCompanyUser(companyId, fromDto(newUser));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    private User fromDto(UserDTO dto) {
        if (dto == null) return null;
        User user = modelMapper.map(dto, User.class);
        user.setEmail(encryptionService.encryptQueryable(dto.getEmail()));
        user.setPhone(encryptionService.encryptQueryable(dto.getPhone()));
        user.setFirstName(encryptionService.encryptQueryable(dto.getFirstName()));
        user.setLastName(encryptionService.encryptQueryable(dto.getLastName()));
        return user;
    }

    private UserDTO toDto(User user) {
        if (user == null) return null;
        UserDTO dto = modelMapper.map(user, UserDTO.class);
        dto.setEmail(encryptionService.decrypt(user.getEmail()));
        dto.setPhone(encryptionService.decrypt(user.getPhone()));
        dto.setFirstName(encryptionService.decrypt(user.getFirstName()));
        dto.setLastName(encryptionService.decrypt(user.getLastName()));
        return dto;
    }

    private Company fromDto(CompanyDTO dto) {
        if (dto == null) return null;
        return modelMapper.map(dto, Company.class);
    }

    private CompanyDTO toDto(Company company) {
        if (company == null) return null;
        return modelMapper.map(company, CompanyDTO.class);
    }
}
