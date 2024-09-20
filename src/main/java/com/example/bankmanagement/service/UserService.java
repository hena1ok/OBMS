package com.example.bankmanagement.service;

import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
	   private final UserRepository userRepository;
	    private final PasswordEncoder passwordEncoder;
	    @Autowired
	    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
	        this.userRepository = userRepository;
	        this.passwordEncoder = passwordEncoder;
	    }

    // Fetches all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public void saveUser1(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encode password before saving
        userRepository.save(user);
    }

    // Fetches a user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Saves a user (could also encode the password here)
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Ensure this is done
        return userRepository.save(user);
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
        return userRepository.findByEmail(email); // Assuming this returns an Optional<User>
    }
}
