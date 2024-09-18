package com.example.bankmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .authorizeRequests()
	        .requestMatchers("/api/**").authenticated() // Protect API endpoints
	        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() // Allow access to static resources
	        .requestMatchers("/").permitAll() // Ensure access to the root URL
	        .anyRequest().permitAll()
	        .and()
	        .formLogin()
	        .and()
	        .httpBasic()
	        .and()
	        .csrf().disable(); // Disable CSRF for simplicity
	    return http.build();
	}


}
