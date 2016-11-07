package com.dsmc.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


class StatelessAuthenticationFilter extends GenericFilterBean {
  private final StatelessTokenService statelessTokenService;

  StatelessAuthenticationFilter(StatelessTokenService statelessTokenService) {
    this.statelessTokenService = statelessTokenService;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    String token = ((HttpServletRequest) request).getHeader(HttpHeaders.AUTHORIZATION);
    statelessTokenService.parseToken(token)
        .ifPresent(claims -> {
          Authentication authentication = getAuthenticationFromClaims(claims);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        });
    chain.doFilter(request, response);
  }

  private Authentication getAuthenticationFromClaims(Map<String, Object> claims) {
    List<GrantedAuthority> grantedAuthorities = null;
    if (claims.containsKey("roles")) {
      grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get("roles").toString());
    }
    return new UsernamePasswordAuthenticationToken(claims.get("username"), null, grantedAuthorities);
  }
}