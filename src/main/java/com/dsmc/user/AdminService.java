package com.dsmc.user;

import com.dsmc.user.domain.Company;
import com.dsmc.user.domain.User;

import java.util.List;

public interface AdminService {
    Company getCompanyById(String companyId);

    List<Company> getCompanies();

    List<Company> getCompanies(Integer page, Integer pageSize);

    Company createCompany(Company company);

    void updateCompany(Company company);

    User getCompanyUser(String companyId, String userId);

    List<User> getCompanyUsers(String companyId);

    User createCompanyUser(String companyId, User user);

    void updateCompanyUser(String companyId, String userId, User user);

    boolean verifyCompanyAccountByEmail(String companyContactEmail, String verificationCode);
}
