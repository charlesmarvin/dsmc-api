package com.dsmc.common.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RandomCodeGeneratorTest {
    private final RandomCodeGenerator randomCodeGenerator = new RandomCodeGenerator();

    @Test
    public void generatePaddedNumericCode() throws Exception {
        for (int i = 0; i < 50; i++) {
            String code = randomCodeGenerator.generatePaddedNumericCode(5);
            assertTrue("should return numeric code of specified length", code.matches("\\d{5}"));
        }
    }

}