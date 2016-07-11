package com.dsmc.user;

import com.dsmc.common.service.EncryptionService;
import com.dsmc.user.domain.Company;
import com.dsmc.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class MongoAdminService implements AdminService {
    private static final int DEFAULT_PAGE_START = 0;
    private static final int DEFAULT_PAGE_SIZE = 100;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    @Autowired
    public MongoAdminService(CompanyRepository companyRepository,
                             UserRepository userRepository,
                             EncryptionService encryptionService) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
    }

    @Override
    public Company getCompanyById(String companyId) {
        return companyRepository.findOne(companyId);
    }

    @Override
    public List<Company> getCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public List<Company> getCompanies(Integer page, Integer pageSize) {
        Pageable pageable = getPageable(page, pageSize);
        Page<Company> companies = companyRepository.findAll(pageable);
        return companies.getContent();
    }

    private Pageable getPageable(Integer page, Integer pageSize) {
        int pageInt = (page == null) ? DEFAULT_PAGE_START : page;
        int pageSizeInt = (pageSize == null) ? DEFAULT_PAGE_SIZE : pageSize;
        return new PageRequest(pageInt, pageSizeInt);
    }

    @Override
    public Company createCompany(Company company) {
        return companyRepository.insert(company);
    }

    @Override
    public void updateCompany(Company company) {
        companyRepository.save(company);
    }

    @Override
    public User getCompanyUser(String companyId, String userId) {
        return userRepository.findOneByCompanyAndUser(companyId, userId);
    }

    @Override
    public List<User> getCompanyUsers(String companyId) {
        return userRepository.findAllByCompany(companyId);
    }

    @Override
    public User createCompanyUser(String companyId, User user) {
        Company company = companyRepository.findOne(companyId);
        user.setCompany(company);
        user.setPassword(new String(Base64.getEncoder().encode(encryptionService.hash(user.getPassword()))));
        return userRepository.insert(user);
    }

    @Override
    public void updateCompanyUser(String companyId, User user) {
        Company company = companyRepository.findOne(companyId);
        user.setCompany(company);
        userRepository.save(user);
    }
}
