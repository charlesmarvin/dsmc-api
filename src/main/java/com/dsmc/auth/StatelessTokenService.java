package com.dsmc.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class StatelessTokenService {
  private final static Logger LOG = LoggerFactory.getLogger(StatelessTokenService.class);
  private final String secret;

  @Autowired
  public StatelessTokenService(@Value("${app.security.encryption.key}") String secret) {
    this.secret = secret;
  }

  public String buildToken(Map<String, Object> claims) {
    LocalDateTime exp = LocalDateTime.now(ZoneId.of("Z")).plus(1, ChronoUnit.DAYS);
    return Jwts.builder()
        .setClaims(claims)
        .setExpiration(Date.from(exp.atZone(ZoneId.systemDefault()).toInstant()))
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  public Map<String, Object> parseToken(String token) {
    try {
      return Jwts.parser()
          .setSigningKey(secret)
          .parseClaimsJws(token)
          .getBody();
    } catch (JwtException e) {
      LOG.warn("Error parsing token", e);
    }
    return null;
  }
}