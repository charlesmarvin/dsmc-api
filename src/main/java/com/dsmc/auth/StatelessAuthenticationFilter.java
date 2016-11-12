package com.dsmc.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    if (httpRequest.getCookies() != null) {
      Stream.of(httpRequest.getCookies())
          .filter(cookie -> cookie.getName().equals("access_token"))
          .findFirst()
          .ifPresent(cookie -> statelessTokenService.parseToken(cookie.getValue())
              .ifPresent(claims -> {
                Authentication authentication = getAuthenticationFromClaims(claims);
                SecurityContextHolder.getContext().setAuthentication(authentication);
              }));
    }
    chain.doFilter(request, response);
  }

  private Authentication getAuthenticationFromClaims(Map<String, Object> claims) {
    List<GrantedAuthority> grantedAuthorities = null;
    if (claims.containsKey("roles")) {
      grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get("roles").toString());
    }
    return new MultiTenantAuthenticationToken(claims.get("username"), null, grantedAuthorities, claims.get("identifier"), claims.get("tenantId"));
  }
}
