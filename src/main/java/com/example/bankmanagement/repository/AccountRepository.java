package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	 boolean existsByAccountNumber(String accountNumber);

	List<Account> findByUserId(Long userId);
	 // Searching by related User properties
    List<Account> findByUserUsernameContainingOrUserPhoneContaining(String username, String phone);

	List<Account> findByUser(User user);

}
