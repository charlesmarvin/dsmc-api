package com.dsmc.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class StatelessLoginFilter extends AbstractAuthenticationProcessingFilter {

  private final StatelessTokenService statelessTokenService;
  private final ObjectMapper objectMapper;

  StatelessLoginFilter(String url,
                       AuthenticationManager authenticationManager,
                       StatelessTokenService statelessTokenService,
                       ObjectMapper objectMapper) {
    super(new AntPathRequestMatcher(url));
    setAuthenticationManager(authenticationManager);
    this.statelessTokenService = statelessTokenService;
    this.objectMapper = objectMapper;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {
    TokenRequest credentials = objectMapper.readValue(request.getInputStream(), TokenRequest.class);
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(credentials.clientId, credentials.clientSecret);
    return getAuthenticationManager().authenticate(token);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain chain,
                                          Authentication authentication)
      throws IOException, ServletException {
    Object principal = authentication.getPrincipal();
    if (principal != null && principal instanceof MultiTenantUser) {
      MultiTenantUser user = (MultiTenantUser) principal;
      Map<String, Object> claims = new HashMap<>();
      claims.put("username", user.getUsername());
      claims.put("identifier", user.getIdentifier());
      claims.put("tenantId", user.getTenantId());
      String token = statelessTokenService.buildToken(claims);
      response.addCookie(createAccessTokenCookie(token));
    }
  }

  protected Cookie createAccessTokenCookie(String token) {
    Cookie cookie = new Cookie("access_token", token);
    cookie.setHttpOnly(true);
    cookie.setSecure(true);
    return cookie;
  }

  private final static class TokenRequest {
    final String clientId;
    final String clientSecret;

    @JsonCreator
    public TokenRequest(@NotBlank @JsonProperty("clientId") String clientId,
                        @NotBlank @JsonProperty("clientSecret") String clientSecret) {
      this.clientId = clientId;
      this.clientSecret = clientSecret;
    }
  }
}