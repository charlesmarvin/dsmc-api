package com.dsmc.user;

import com.dsmc.common.domain.Status;
import com.dsmc.common.event.EventCatalogue;
import com.dsmc.common.event.Publisher;
import com.dsmc.common.service.encryption.EncryptionService;
import com.dsmc.common.service.passcode.PasscodeService;
import com.dsmc.user.domain.Company;
import com.dsmc.user.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoAdminService implements AdminService {
  private static final int DEFAULT_PAGE_START = 0;
  private static final int DEFAULT_PAGE_SIZE = 100;
  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final PasscodeService passcodeService;
  private final PasswordEncoder passwordEncoder;
  private final Publisher publisher;
  private final EncryptionService encryptionService;

  @Autowired
  public MongoAdminService(CompanyRepository companyRepository,
                           UserRepository userRepository,
                           EncryptionService encryptionService,
                           PasscodeService passcodeService,
                           PasswordEncoder passwordEncoder,
                           Publisher publisher) {
    this.companyRepository = companyRepository;
    this.userRepository = userRepository;
    this.encryptionService = encryptionService;
    this.passcodeService = passcodeService;
    this.passwordEncoder = passwordEncoder;
    this.publisher = publisher;
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
    //TODO mc - find a better way to model bidirectional relationship
    company.setVerified(false);
    company.setStatus(Status.Unverified);
    User newUser = company.getContact();
    newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
    newUser = userRepository.save(newUser);
    company.setContact(newUser);
    Company newCompany = companyRepository.insert(company);
    newUser.setCompany(newCompany);
    userRepository.save(newUser);
    sendVerificationEmail(newCompany);
    publisher.publish(EventCatalogue.USER_CREATED, newUser);
    return newCompany;
  }

  private void sendVerificationEmail(Company company) {
    passcodeService.generate(company.getId(), encryptionService.decrypt(company.getContact().getEmail()));
  }

  @Override
  public void updateCompany(Company company) {
    Company companyFromDb = companyRepository.findOne(company.getId());
    if (companyFromDb == null) {
      return;
    }
    if (company.getName() != null) {
      companyFromDb.setName(company.getName());
    }
    if (company.getStatus() != null) {
      companyFromDb.setStatus(company.getStatus());
    }
    companyRepository.save(companyFromDb);
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
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.insert(user);
  }

  @Override
  public void updateCompanyUser(String companyId, User userUpdates) {
    User user = userRepository.findOne(userUpdates.getId());
    if (user == null || user.getCompany() == null || !companyId.equals(user.getCompany().getId())) {
      throw new RuntimeException("Illegal attempt to update user"); //TODO make exception class
    }
    if (userUpdates.getFirstName() != null) {
      user.setFirstName(userUpdates.getFirstName());
    }
    if (userUpdates.getLastName() != null) {
      user.setLastName(userUpdates.getLastName());
    }
    if (userUpdates.getEmail() != null) {
      user.setEmail(userUpdates.getEmail());
    }
    if (userUpdates.getPhone() != null) {
      user.setPhone(userUpdates.getPhone());
    }
    if (userUpdates.getUsername() != null) {
      user.setUsername(userUpdates.getUsername());
    }
    if (userUpdates.getPassword() != null) {
      user.setPassword(passwordEncoder.encode(userUpdates.getPassword()));
    }
    userRepository.save(user);
    publisher.publish(EventCatalogue.USER_UPDATED, user);
  }

  @Override
  public boolean resendCompanyAccountVerificationByEmail(String companyContactEmail) {
    Company company = companyRepository.findByEmail(companyContactEmail);
    if (company != null && company.getStatus() == Status.Unverified) {
      sendVerificationEmail(company);
      return true;
    }
    return false;
  }

  @Override
  public boolean verifyCompanyAccountByEmail(String companyContactEmail, String verificationCode) {
    Company company = companyRepository.findByEmail(companyContactEmail);
    if (company == null) {
      return false;
    }
    if (passcodeService.validate(company.getId(), verificationCode)) {
      company.setStatus(Status.Active);
      company.setVerified(true);
      companyRepository.save(company);
      return true;
    }
    return false;
  }
}
