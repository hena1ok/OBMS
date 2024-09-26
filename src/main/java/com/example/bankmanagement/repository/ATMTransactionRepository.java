package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.ATMTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ATMTransactionRepository extends JpaRepository<ATMTransaction, Integer> {
    List<ATMTransaction> findByAccountId(int accountId);
}
