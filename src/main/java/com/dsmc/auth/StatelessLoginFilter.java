package com.dsmc.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class StatelessLoginFilter extends AbstractAuthenticationProcessingFilter {
  private final StatelessTokenService statelessTokenService;
  private final AuthenticationClaimTokenMapper authenticationClaimTokenMapper;
  private final IdentityService identityService;
  private final ObjectMapper objectMapper;

  StatelessLoginFilter(String url,
                       AuthenticationManager authenticationManager,
                       StatelessTokenService statelessTokenService,
                       AuthenticationClaimTokenMapper authenticationClaimTokenMapper,
                       IdentityService identityService,
                       ObjectMapper objectMapper) {
    super(new AntPathRequestMatcher(url));
    this.statelessTokenService = statelessTokenService;
    this.authenticationClaimTokenMapper = authenticationClaimTokenMapper;
    this.identityService = identityService;
    this.objectMapper = objectMapper;
    setAuthenticationManager(authenticationManager);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {
    ClientCredentials credentials = objectMapper.readValue(request.getInputStream(), ClientCredentials.class);
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(credentials.clientId, credentials.clientSecret);
    return getAuthenticationManager().authenticate(token);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain chain,
                                          Authentication authentication)
      throws IOException, ServletException {
    identityService.findByUsername(authentication.getName())
        .ifPresent(identity -> {
          Map<String, Object> claims = authenticationClaimTokenMapper.fromIdentity(identity);
          String token = statelessTokenService.buildToken(claims);
          response.addHeader(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token));
        });
  }


  private final static class ClientCredentials {
    final String clientId;
    final String clientSecret;

    @JsonCreator
    public ClientCredentials(@NotBlank @JsonProperty("clientId") String clientId,
                             @NotBlank @JsonProperty("clientSecret") String clientSecret) {
      this.clientId = clientId;
      this.clientSecret = clientSecret;
    }
  }
}