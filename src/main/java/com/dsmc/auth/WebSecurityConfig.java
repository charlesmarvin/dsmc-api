package com.dsmc.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  private UserDetailsService userDetailsService;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private StatelessTokenService statelessTokenService;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private IdentityService identityService;
  @Value("${app.security.admin.username}")
  private String adminUsername;
  @Value("${app.security.admin.password}")
  private String adminPassword;
  @Value("${app.security.admin.role}")
  private String adminRole;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    Filter loginFilter = new StatelessLoginFilter("/api/auth/token",
        authenticationManager(),
        statelessTokenService,
        objectMapper);

    AdminStatelessLoginFilter adminLoginFilter = new AdminStatelessLoginFilter("/api/admin/auth/token",
        authenticationManager(),
        statelessTokenService,
        objectMapper);

    StatelessAuthenticationFilter authFilter = new StatelessAuthenticationFilter(statelessTokenService);
    http.csrf()
        .disable();

    http.authorizeRequests()
        .regexMatchers("(/api/company/verify|/api/auth/token)").permitAll()
        .antMatchers("/api/admin/**").hasRole(adminRole)
        .antMatchers("/api/**").authenticated()
        .and()
        .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(adminLoginFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }


  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder);

    auth.inMemoryAuthentication().withUser(adminUsername).password(adminPassword).roles(adminRole);

  }

}
