package com.dsmc.user;

import com.dsmc.user.domain.Company;
import com.dsmc.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by charlesmarvin on 7/9/16.
 */
@Service
public class MongoAdminService implements AdminService {
    private static final int DEFAULT_PAGE_START = 0;
    private static final int DEFAULT_PAGE_SIZE = 100;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Autowired
    public MongoAdminService(CompanyRepository companyRepository, 
                             UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Company getCompanyById(String companyId) {
        return companyRepository.findOne(companyId);
    }

    @Override
    public List<Company> getCompanies() {
        return getCompanies(DEFAULT_PAGE_START, DEFAULT_PAGE_SIZE);
    }

    @Override
    public List<Company> getCompanies(int page, int pageSize) {
        Pageable pageable = new PageRequest(page, pageSize);
        Page<Company> companies = companyRepository.findAll(pageable);
        return companies.getContent();
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
        return userRepository.insert(user);
    }

    @Override
    public void updateCompanyUser(String companyId, User user) {
        Company company = companyRepository.findOne(companyId);
        user.setCompany(company);
        userRepository.save(user);
    }
}