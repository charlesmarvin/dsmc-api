package com.dsmc.config;

import com.dsmc.auth.AuthenticationClaimTokenMapper;
import com.dsmc.auth.StatelessAuthenticationFilter;
import com.dsmc.auth.StatelessLoginFilter;
import com.dsmc.auth.StatelessTokenService;

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

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  @Autowired
  UserDetailsService userDetailsService;
  @Autowired
  PasswordEncoder passwordEncoder;
  @Autowired
  private StatelessTokenService statelessTokenService;
  @Autowired
  private AuthenticationClaimTokenMapper authenticationClaimTokenMapper;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf()
        .disable()
        .antMatcher("/api/**")
        .authorizeRequests()
        .antMatchers("/api/v1/auth/token")
        .permitAll()
        .anyRequest()
        .authenticated()
        .and()
        .addFilterBefore(new StatelessLoginFilter("/api/v1/auth/token", authenticationManager(), statelessTokenService),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(new StatelessAuthenticationFilter(statelessTokenService, authenticationClaimTokenMapper),
            UsernamePasswordAuthenticationFilter.class)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }


  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder)
        .and()
        .inMemoryAuthentication()
        .withUser("user")
        .password("password")
        .roles("USER");
  }

}
