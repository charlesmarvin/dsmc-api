package com.dsmc.user;

import com.dsmc.common.adapters.email.EmailAdapter;
import com.dsmc.common.domain.Status;
import com.dsmc.common.service.EncryptionService;
import com.dsmc.common.util.RandomCodeGenerator;
import com.dsmc.user.domain.Company;
import com.dsmc.user.domain.User;
import com.dsmc.user.domain.Verification;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MongoAdminService implements AdminService {
  private static final int DEFAULT_PAGE_START = 0;
  private static final int DEFAULT_PAGE_SIZE = 100;
  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final VerificationRepository verificationRepository;
  private final RandomCodeGenerator randomCodeGenerator;
  private final int verificationCodeLength;
  private final EncryptionService encryptionService;
  private final EmailAdapter emailAdapter;

  @Autowired
  public MongoAdminService(CompanyRepository companyRepository,
                           UserRepository userRepository,
                           VerificationRepository verificationRepository,
                           EncryptionService encryptionService,
                           EmailAdapter emailAdapter,
                           RandomCodeGenerator randomCodeGenerator,
                           @Value("${app.verification-code.length}") int verificationCodeLength) {
    this.companyRepository = companyRepository;
    this.userRepository = userRepository;
    this.encryptionService = encryptionService;
    this.emailAdapter = emailAdapter;
    this.verificationRepository = verificationRepository;
    this.randomCodeGenerator = randomCodeGenerator;
    this.verificationCodeLength = verificationCodeLength;
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
    User newUser = userRepository.save(company.getContact());
    company.setContact(newUser);
    Company newCompany = companyRepository.insert(company);
    newUser.setCompany(newCompany);
    userRepository.save(newUser);
    sendVerificationEmail(newCompany);
    return newCompany;
  }

  private void sendVerificationEmail(Company company) {
    //TODO the below should be done off the back of a queued message
    // raise the message here and create an async handler to consume and process it
    Verification verification = new Verification();
    verification.setCompanyId(company.getId());
    String companyContactEmail = encryptionService.decrypt(company.getContact().getEmail());
    verification.setIdentifier(companyContactEmail);
    verification.setVerificationCode(randomCodeGenerator.generatePaddedNumericCode(verificationCodeLength));
    verificationRepository.insert(verification);
    try {
      Map<String, Object> data = new HashMap<>();
      data.put("code", verification.getVerificationCode());
      data.put("name", company.getName());
      emailAdapter.send("Client Services <clientservices@24sixty.io>",
          companyContactEmail,
          "Welcome",
          "onboarding-company-account-verification", //TODO Externalize this
          data);
    } catch (Exception e) {
      throw new RuntimeException("Error sending verification email", e);
    }
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
    user.setPassword(encryptionService.hashBase64(user.getPassword()));
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
      user.setPassword(encryptionService.hashBase64(userUpdates.getPassword()));
    }
    userRepository.save(user);
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
    Verification verification = verificationRepository.findByIdentifier(companyContactEmail);
    if (verification == null) {
      return false;
    }
    if (StringUtils.equals(verificationCode, verification.getVerificationCode())) {
      Company company = companyRepository.findOne(verification.getCompanyId());
      if (company == null) {
        return false;
      }
      company.setStatus(Status.Active);
      company.setVerified(true);
      companyRepository.save(company);
      verificationRepository.delete(verification.getId());
      return true;
    } else {
      verification.setVerificationAttempts(verification.getVerificationAttempts() + 1);
      verificationRepository.save(verification);
      return false;
    }
  }
}
