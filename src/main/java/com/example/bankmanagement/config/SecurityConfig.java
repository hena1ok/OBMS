package com.example.bankmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests()
                .requestMatchers("/signup", "/login").permitAll() // Allow access to login and signup pages
                .anyRequest().authenticated() // Require authentication for all other requests
            .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home", true) // Redirect to /home after successful login
                .permitAll()
            .and()
            .logout()
                .permitAll();

        return http.build();
    }


}
