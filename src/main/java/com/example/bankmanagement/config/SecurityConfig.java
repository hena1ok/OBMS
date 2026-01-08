package com.example.bankmanagement.config;

import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.bankmanagement.service.CustomLogoutSuccessHandler;
import com.example.bankmanagement.service.CustomUserDetailsService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    @Lazy
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomLogoutSuccessHandler logoutSuccessHandler; // Autowire your logout handler

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().and()
            .authorizeHttpRequests()
                .requestMatchers("/signup", "/login").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/teller/**").hasRole("TELLER")
                .requestMatchers("/customer/**").hasRole("USER")
                .requestMatchers("/support/**").hasAnyRole("SUPPORT","ADMIN")
                .requestMatchers("/atm/**").hasAnyRole("USER","SUPPORT","TELLER","ADMIN")
                .requestMatchers("/header").hasAnyRole("USER") // Access for user roles
                .requestMatchers("/admin-header").hasRole("ADMIN") // Access for admin role
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .successHandler(customSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(logoutSuccessHandler) // Use the custom handler
            .and()
            .exceptionHandling()
                .accessDeniedPage("/access-denied");

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                Map<String, String> roleRedirectMap = Map.of(
                        "ROLE_ADMIN", "/admin/dashboard",
                        "ROLE_TELLER", "/teller/dashboard",
                        "ROLE_USER", "/customer/dashboard",
                        "ROLE_SUPPORT", "/support/dashboard"
                );

                boolean redirected = false;
                for (var grantedAuthority : authentication.getAuthorities()) {
                    String redirectUrl = roleRedirectMap.get(grantedAuthority.getAuthority());
                    if (redirectUrl != null) {
                        response.sendRedirect(redirectUrl);
                        redirected = true;
                        break;
                    }
                }
                if (!redirected) {
                    response.sendRedirect("/home");
                }
            }
        };
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}
