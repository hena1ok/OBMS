package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.ATM;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ATMRepository extends JpaRepository<ATM, Long> {
    // Add custom query methods if necessary
}
