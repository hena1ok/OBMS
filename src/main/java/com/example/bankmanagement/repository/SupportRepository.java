package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Support;
import com.example.bankmanagement.model.SupportStats;
import com.example.bankmanagement.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupportRepository extends JpaRepository<Support, Long> {

	void deleteByUser(User user);

	
}
