package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Role;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.RoleRepository;
import com.example.bankmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private static UserRepository userRepository;
    private RoleRepository roleRepository;
    private  PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Fetches all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Fetches a user by ID
    public static User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Deletes a user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Checks if a user with the given email exists
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    // Finds a user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Finds a user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Checks if a username exists
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // Checks if an email exists
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Saves a user with the default role "ROLE_USER"
    public User saveUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Assign default role "ROLE_USER" if roles are not already set
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow(() -> 
                new RuntimeException("Error: Role is not found."));
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);
        }

        return userRepository.save(user);
    }
}
