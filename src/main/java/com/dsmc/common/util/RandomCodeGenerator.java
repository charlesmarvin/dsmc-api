package com.dsmc.common.util;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class RandomCodeGenerator {
  public String generatePaddedNumericCode(int length) {
    int maxValue = ((int) Math.pow(10, length)) - 1;
    return String.format("%0" + length + "d", ThreadLocalRandom.current().nextInt(maxValue));
  }
}
