package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByUserId(Long userId); // Assuming you add a User reference in Transaction
    List<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
