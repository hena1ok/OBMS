package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.Role;
import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.AccountRepository;
import com.example.bankmanagement.repository.RoleRepository;
import com.example.bankmanagement.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

   private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.accountRepository = accountRepository;
    }

    public void updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser != null) {
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setAddress(user.getAddress());
            existingUser.setDob(user.getDob());
            existingUser.setAccountType(user.getAccountType());
            userRepository.save(existingUser);
        }
    }

    public List<User> getRecentUsers() {
        return userRepository.findTop10ByOrderByRegistrationDateDesc(); // Adjust query as necessary
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    // Fetch the currently logged-in user
    public Optional<User> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username);
        }
        return Optional.empty();
    }

    // Fetch all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Fetch a user by ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    // Delete a user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Check if a user with the given email exists
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    // Check if a user with the given username exists
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // Check if a user with the given email exists
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Find a user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Find a user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public List<User> findAll() {
        return userRepository.findAll(); // Assuming you have a method in UserRepository
    }

    // Save a user and assign roles
    @Transactional
    public User saveUser(@Valid User user, Set<String> roleNames) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRegistrationDate(LocalDateTime.now()); 
        Set<Role> roles = new HashSet<>();
        if (roleNames == null || roleNames.isEmpty()) {
            Role defaultRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Default role 'ROLE_USER' not found."));
            roles.add(defaultRole);
        } else {
            for (String roleName : roleNames) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Error: Role '" + roleName + "' not found."));
                roles.add(role);
            }
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }

    // Update an existing user's profile
    public void updateUserProfile(Long userId, User updatedUser) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            userRepository.save(user);
        } else {
            throw new RuntimeException("Error: User not found.");
        }
    }

    // Fetch accounts associated with a user by ID
    public List<Account> getAccountsByUserId(Long userId) {
        return accountRepository.findByUserId(userId);
    }

    // Fetch a user by ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    public User saveUser(User user) {
        return userRepository.save(user); // This should return the saved User object
    }

    // Administrative method to assign roles to an existing user
    @Transactional
    public void assignRolesToUser(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Error: Role '" + roleName + "' not found."));
            roles.add(role);
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    // Administrative method to remove a role from a user
    @Transactional
    public void removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User not found."));
        
        Role roleToRemove = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Error: Role '" + roleName + "' not found."));

        Set<Role> roles = user.getRoles();
        roles.remove(roleToRemove);
        user.setRoles(roles);

        userRepository.save(user);
    }

    // Administrative method to deactivate (disable) a user account
    public void deactivateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(false);  // Assuming the User entity has an 'enabled' field
            userRepository.save(user);
        } else {
            throw new RuntimeException("Error: User not found.");
        }
    }

    // Administrative method to reactivate (enable) a user account
    public void activateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(true);  // Assuming the User entity has an 'enabled' field
            userRepository.save(user);
        } else {
            throw new RuntimeException("Error: User not found.");
        }
    }

    // Fetch all roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    public List<String> getAllUserEmails() {
        return userRepository.findAllUserEmails();
    }
    public List<User> getUsersByRoles(List<String> roles) {
        // Fetch users from the repository based on the provided roles
        return userRepository.findByRolesIn(roles);
    }

}

