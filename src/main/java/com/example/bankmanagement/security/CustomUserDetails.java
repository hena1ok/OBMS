package com.example.bankmanagement.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.bankmanagement.model.Role;
import com.example.bankmanagement.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private final User user; // The user entity

   

    public CustomUserDetails(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        this.user = user;
    }

    public CustomUserDetails(Long id, String username, String password, List<SimpleGrantedAuthority> authorities) {
        this.user = new User();
        this.user.setId(id);
        this.user.setUsername(username);
        this.user.setPassword(password);
        this.user.setRoles((Set<Role>) authorities.stream()
                .map(auth -> new Role(null)) // Assuming Role has (id, name)
                .collect(Collectors.toList()));
    }

	// Updated method to get the user's roles as a List
    public List<Role> getRoles() {
        return Optional.ofNullable(user.getRoles())
                       .map(List::copyOf) // Create a copy to avoid external modification
                       .orElse(List.of()); // Return an empty list if roles are null
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Customize based on your business logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Customize based on your business logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Customize based on your business logic
    }

    @Override
    public boolean isEnabled() {
        return true; // Customize based on your business logic
    }

    // New method to get the user's ID
    public Long getId() {
        return user.getId(); // Assuming User has a getId() method
    }

    public User getUser() {
        return user; // Provide access to the User object
    }

    // Method to check if user has a specific role
    public boolean hasRole(String roleName) {
        return getRoles().stream()
                         .anyMatch(role -> role.getName().equals(roleName));
    }

    @Override
    public String toString() {
        return String.format("CustomUserDetails{username='%s', authorities=%s}", user.getUsername(), getAuthorities());
    }
    
}

