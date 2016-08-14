package com.dsmc.common.domain;

public class SecureValue {
  private byte[] value;

  public SecureValue(byte[] value) {
    this.value = value;
  }

  public SecureValue(String value) {
    this(value.getBytes());
  }

  public SecureValue() {
  }

  public byte[] getValue() {
    return value;
  }
}
