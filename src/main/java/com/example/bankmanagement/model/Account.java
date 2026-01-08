package com.example.bankmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bank account with various attributes and relationships.
 */
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber; 

    private String accountName;

    @NotNull(message = "Account type cannot be null")
    private String accountType; // e.g., Savings, Checking

    @NotNull(message = "Balance cannot be null")
    @Positive(message = "Balance must be positive")
    private BigDecimal balance;

    private LocalDate dateOpened;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> sourceTransactions = new ArrayList<>(); // Transactions where this account is the source

    @OneToMany(mappedBy = "destinationAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> destinationTransactions = new ArrayList<>(); // Transactions where this account is the destination

    // Default constructor for JPA
    public Account() {
    }

    // Getters and Setters

    public Account(Long id) {
        this.id = id;
    }

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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDate getDateOpened() {
        return dateOpened;
    }

    public void setDateOpened(LocalDate dateOpened) {
        this.dateOpened = dateOpened;
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

    /**
     * Deposits a given amount into the account.
     * 
     * @param amount The amount to deposit.
     * @throws IllegalArgumentException if the amount is negative or zero.
     */
    public void deposit(BigDecimal amount) {
        validateAmount(amount);
        this.balance = this.balance.add(amount);
    }

    /**
     * Withdraws a given amount from the account.
     * 
     * @param amount The amount to withdraw.
     * @throws IllegalArgumentException if the amount is greater than the balance or negative.
     */
    public void withdraw(BigDecimal amount) {
        validateAmount(amount);
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        this.balance = this.balance.subtract(amount);
    }

    /**
     * Validates the amount for deposit and withdrawal operations.
     * 
     * @param amount The amount to validate.
     * @throws IllegalArgumentException if the amount is not positive.
     */
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    @Override
    public String toString() {
        return String.format("Account{id=%d, accountNumber='%s', accountType='%s', balance=%.2f, userId=%s}",
                id, accountNumber, accountType, balance, (user != null ? user.getId() : "null"));
    }
}
