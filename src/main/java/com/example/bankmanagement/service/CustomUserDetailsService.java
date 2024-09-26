package com.example.bankmanagement.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user by username
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        // Check if user is present; if not, throw an exception
        User user = userOptional.orElseThrow(() -> 
            new UsernameNotFoundException("User not found with username: " + username)
        );

        // Return a UserDetails object for Spring Security
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            new ArrayList<>()
        );
    }
}
