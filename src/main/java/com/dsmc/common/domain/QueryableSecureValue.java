package com.dsmc.common.domain;

public class QueryableSecureValue extends SecureValue {

  private byte[] hash;

  public QueryableSecureValue() {
  }

  public QueryableSecureValue(String value, String hash) {
    this(value.getBytes(), hash.getBytes());
  }

  public QueryableSecureValue(byte[] value, byte[] hash) {
    super(value);
    this.hash = hash;
  }

  public byte[] getHash() {
    return hash;
  }
}
