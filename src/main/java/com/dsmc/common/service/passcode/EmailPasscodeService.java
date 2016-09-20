package com.dsmc.common.service.passcode;

import com.dsmc.common.adapters.email.EmailAdapter;
import com.dsmc.common.util.RandomCodeGenerator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class EmailPasscodeService implements PasscodeService {
  private final String passcodeEmailTemplateId;
  private final int verificationCodeLength;
  private RandomCodeGenerator randomCodeGenerator;
  private OneTimePasscodeRepository oneTimePasscodeRepository;
  private EmailAdapter emailAdapter;

  @Autowired
  public EmailPasscodeService(RandomCodeGenerator randomCodeGenerator,
                              OneTimePasscodeRepository oneTimePasscodeRepository,
                              EmailAdapter emailAdapter,
                              @Value("${app.passcode.emailTemplateId}")
                                  String passcodeEmailTemplateId,
                              @Value("${app.passcode.length}")
                                  int verificationCodeLength) {
    this.randomCodeGenerator = randomCodeGenerator;
    this.oneTimePasscodeRepository = oneTimePasscodeRepository;
    this.emailAdapter = emailAdapter;
    this.passcodeEmailTemplateId = passcodeEmailTemplateId;
    this.verificationCodeLength = verificationCodeLength;
  }

  @Override
  public void generate(String identifier, String verificationTarget) {
    OneTimePasscode oneTimePasscode = new OneTimePasscode();
    oneTimePasscode.setIdentifier(identifier);
    oneTimePasscode.setPasscode(randomCodeGenerator.generatePaddedNumericCode(verificationCodeLength));
    oneTimePasscodeRepository.insert(oneTimePasscode);
    try {
      Map<String, Object> templateData = Collections.singletonMap("code", oneTimePasscode.getPasscode());
      emailAdapter.send("Client Services <clientservices@24sixty.io>",
          verificationTarget,
          "Welcome",
          passcodeEmailTemplateId,
          templateData);
    } catch (Exception e) {
      throw new RuntimeException("Error sending oneTimePasscode email", e);
    }
  }

  @Override
  public boolean validate(String identifier, String passcode) {
    OneTimePasscode oneTimePasscode = oneTimePasscodeRepository.findByIdentifier(identifier);
    if (oneTimePasscode == null) {
      return false;
    }
    if (StringUtils.equals(passcode, oneTimePasscode.getPasscode())) {
      oneTimePasscodeRepository.delete(oneTimePasscode.getId());
      return true;
    }
    return false;
  }
}
