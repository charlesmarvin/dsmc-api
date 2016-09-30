package com.dsmc.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


class StatelessAuthenticationFilter extends GenericFilterBean {
  private final StatelessTokenService statelessTokenService;
  private final AuthenticationClaimTokenMapper authenticationClaimTokenMapper;

  StatelessAuthenticationFilter(StatelessTokenService statelessTokenService,
                                AuthenticationClaimTokenMapper authenticationClaimTokenMapper) {
    this.statelessTokenService = statelessTokenService;
    this.authenticationClaimTokenMapper = authenticationClaimTokenMapper;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String token = ((HttpServletRequest) request).getHeader(HttpHeaders.AUTHORIZATION);
    if (token != null && token.startsWith("Bearer ")) {
      Map<String, Object> claims = statelessTokenService.parseToken(token.substring(7));
      SecurityContextHolder.getContext().setAuthentication(authenticationClaimTokenMapper.fromClaims(claims));
    }
    chain.doFilter(request, response);
  }
}
