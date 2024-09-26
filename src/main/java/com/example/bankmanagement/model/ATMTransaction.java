package com.example.bankmanagement.model;

//import javax.persistence.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "atm_transactions")
public class ATMTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private double amount;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    // Constructor
    public ATMTransaction(Long long1, String transactionType, double amount, LocalDateTime transactionDate) {
        this.accountId = long1;
        this.transactionType = TransactionType.valueOf(transactionType);
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    // Getters and setters...

    // Enum for transaction types
    public enum TransactionType {
        WITHDRAWAL, DEPOSIT, TRANSFER
    }
    public ATMTransaction() {
        // Default constructor
    }
}
