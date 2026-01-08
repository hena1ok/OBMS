package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find a user by username
    Optional<User> findByUsername(String username);

    // Check if a user exists by username
    boolean existsByUsername(String username);

    // Find a user by email
    Optional<User> findByEmail(String email);

    // Check if a user exists by email
    boolean existsByEmail(String email);

	List<User> findTop10ByOrderByRegistrationDateDesc();

	List<User> findByUsernameContainingIgnoreCase(String query);

	  @Query("SELECT u.email FROM User u")
	    List<String> findAllUserEmails();

	List<User> findByRolesIn(List<String> roles);
	
}
