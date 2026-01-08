package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    // Additional query methods if needed
}
