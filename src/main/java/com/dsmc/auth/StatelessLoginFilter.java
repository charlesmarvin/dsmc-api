package com.dsmc.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class StatelessLoginFilter extends AbstractAuthenticationProcessingFilter {
  private final TypeReference<Map<String, Object>> GENERIC_CLAIM_TYPE = new TypeReference<Map<String, Object>>() {
  };

  private final StatelessTokenService statelessTokenService;
  private final IdentityService identityService;
  private final ObjectMapper objectMapper;

  StatelessLoginFilter(String url,
                       AuthenticationManager authenticationManager,
                       StatelessTokenService statelessTokenService,
                       IdentityService identityService,
                       ObjectMapper objectMapper) {
    super(new AntPathRequestMatcher(url));
    this.statelessTokenService = statelessTokenService;
    this.identityService = identityService;
    this.objectMapper = objectMapper;
    setAuthenticationManager(authenticationManager);
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
      response.addHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token));
    }
  }

  private Map<String, Object> getClaimsFromIdentity(Identity identity) {
    try {
      String json = objectMapper.writerWithView(Identity.View.Principal.class)
          .writeValueAsString(identity);
      return objectMapper.readValue(json, GENERIC_CLAIM_TYPE);
    } catch (IOException e) {
      return null;
    }
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