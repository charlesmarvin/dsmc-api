package com.dsmc.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class AdminStatelessLoginFilter extends StatelessLoginFilter {
  private final StatelessTokenService statelessTokenService;

  AdminStatelessLoginFilter(String url,
                            AuthenticationManager authenticationManager,
                            StatelessTokenService statelessTokenService,
                            ObjectMapper objectMapper) {
    super(url, authenticationManager, null, null, objectMapper);
    this.statelessTokenService = statelessTokenService;
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain chain,
                                          Authentication authentication)
      throws IOException, ServletException {
    String roles = getRolesFromAuthentication(authentication);
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", authentication.getName());
    claims.put("roles", roles);
    String token = statelessTokenService.buildToken(claims);
    response.addHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token));
  }

  private String getRolesFromAuthentication(Authentication authentication) {
    return authentication.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(", "));
  }
}
