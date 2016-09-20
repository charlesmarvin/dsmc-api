package com.dsmc.common.service.passcode;

/**
 * Created by charlesmarvin on 9/2/16.
 */
public interface PasscodeService {
  void generate(String identifier, String verificationTarget);

  boolean validate(String identifier, String passcode);
}
