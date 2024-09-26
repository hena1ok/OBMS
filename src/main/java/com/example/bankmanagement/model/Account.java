package com.example.bankmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Account number cannot be null")
    private String accountNumber;

    @NotNull(message = "Account type cannot be null")
    private String accountType; // e.g., Savings, Checking

    @NotNull(message = "Balance cannot be null")
    @Positive(message = "Balance must be positive")
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Relationship with User

    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL)
    private List<Transaction> sourceTransactions = new ArrayList<>(); // Transactions where this account is the source

    @OneToMany(mappedBy = "destinationAccount", cascade = CascadeType.ALL)
    private List<Transaction> destinationTransactions = new ArrayList<>(); // Transactions where this account is the destination

    // Default constructor
    public Account() {
        // For JPA
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Transaction> getSourceTransactions() {
        return sourceTransactions;
    }

    public void setSourceTransactions(List<Transaction> sourceTransactions) {
        this.sourceTransactions = sourceTransactions;
    }

    public List<Transaction> getDestinationTransactions() {
        return destinationTransactions;
    }

    public void setDestinationTransactions(List<Transaction> destinationTransactions) {
        this.destinationTransactions = destinationTransactions;
    }

    // Method to deposit an amount into the account
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            // Create and add a deposit transaction
            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setDate(LocalDate.now()); // Set to today's date
            transaction.setSourceAccount(null); // No source for deposit
            transaction.setDestinationAccount(this);
            transaction.setUser(this.user); // Assuming the user is the one making the deposit
            sourceTransactions.add(transaction);
        } else {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
    }

    // Method to withdraw an amount from the account
    public void withdraw(double amount) {
        if (amount > 0 && amount <= this.balance) {
            this.balance -= amount;
            // Create and add a withdrawal transaction
            Transaction transaction = new Transaction();
            transaction.setAmount(-amount); // Negative amount for withdrawal
            transaction.setDate(LocalDate.now()); // Set to today's date
            transaction.setSourceAccount(this);
            transaction.setDestinationAccount(null); // No destination for withdrawal
            transaction.setUser(this.user); // Assuming the user is the one making the withdrawal
            sourceTransactions.add(transaction);
        } else {
            throw new IllegalArgumentException("Insufficient balance or invalid withdrawal amount");
        }
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", user=" + (user != null ? user.getId() : null) +
                '}';
    }
}
