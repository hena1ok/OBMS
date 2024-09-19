package com.example.bankmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                .requestMatchers("/login", "/error", "/css/**", "/js/**", "/images/**").permitAll()  // Allow access to login and static resources
                .anyRequest().authenticated())
            .formLogin(login -> login
                .loginPage("/login")  // Point to custom login page
                .defaultSuccessUrl("/home", true)  // Redirect to home after successful login
                .permitAll())
            .logout(logout -> logout
                .logoutUrl("/logout")
                .permitAll())
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/error"));  // Handle access denied errors

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var userDetailsManager = new InMemoryUserDetailsManager();
        
        @SuppressWarnings("deprecation")
		var user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("USER")
            .build();
        
        userDetailsManager.createUser(user);
        return userDetailsManager;
    }
}
