package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId); // Fetch transactions by user ID

    List<Transaction> findBySourceAccountId(Long sourceAccountId); // Fetch transactions by source account ID

    List<Transaction> findByDestinationAccountId(Long destinationAccountId); // Fetch transactions by destination account ID

    List<Transaction> findByDateBetween(LocalDate startDate, LocalDate endDate); // Fetch transactions within a date range

    List<Transaction> findByAccountId(Long accountId); // Fetch transactions by account ID (You may want to change this if necessary)
}
