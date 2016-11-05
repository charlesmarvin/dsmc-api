package com.dsmc.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private static final String TOKEN_API_ENDPOINT = "/api/v1/auth/token";
  @Autowired
  private UserDetailsService userDetailsService;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private StatelessTokenService statelessTokenService;
  @Autowired
  private AuthenticationClaimTokenMapper authenticationClaimTokenMapper;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private IdentityService identityService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    Filter loginFilter = new StatelessLoginFilter(TOKEN_API_ENDPOINT,
        authenticationManager(),
        statelessTokenService,
        authenticationClaimTokenMapper,
        identityService,
        objectMapper);
    StatelessAuthenticationFilter authFilter = new StatelessAuthenticationFilter(statelessTokenService, authenticationClaimTokenMapper);
    http.csrf()
        .disable()
        .antMatcher("/api/**")
        .authorizeRequests()
        .antMatchers(TOKEN_API_ENDPOINT)
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }


  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder);
  }

}
