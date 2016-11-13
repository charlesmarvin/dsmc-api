package com.dsmc.user;

import com.dsmc.auth.MultiTenantAuthenticationToken;
import com.dsmc.common.service.encryption.EncryptionService;
import com.dsmc.user.domain.User;
import com.dsmc.user.dto.ProfileDTO;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("api/dashboard")
@SuppressWarnings("unused")
public class DashboardController {

  private final ModelMapper modelMapper;
  private final AdminService adminService;

  @Autowired
  public DashboardController(AdminService adminService,
                             EncryptionService encryptionService,
                             ModelMapper modelMapper) {
    this.adminService = adminService;
    this.modelMapper = modelMapper;
  }

  @RequestMapping(method = RequestMethod.GET, path = "/profile")
  public ProfileDTO get(Authentication principal) {
    MultiTenantAuthenticationToken multiTenantUser = (MultiTenantAuthenticationToken) principal;
    User user = adminService.getCompanyUser(multiTenantUser.getTenantId(), multiTenantUser.getUserId());
    return modelMapper.map(user, ProfileDTO.class);
  }
}
