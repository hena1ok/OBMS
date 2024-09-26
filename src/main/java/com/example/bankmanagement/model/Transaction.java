package com.example.bankmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double amount; // The amount of money transferred

    @NotNull(message = "Date cannot be null")
    private LocalDate date; // Date of the transaction

    private String description; // Optional description of the transaction

    @ManyToOne // The source account for the transaction
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount; // The account from which funds are deducted

    @ManyToOne // The destination account for the transaction
    @JoinColumn(name = "destination_account_id", nullable = false)
    private Account destinationAccount; // The account to which funds are added

    @ManyToOne // The user associated with the transaction
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user who initiated the transaction

    
    @Column(name = "account_id")
    private Long accountId;
    // Default constructor
    public Transaction() {
        // For JPA
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Account getSourceAccount() {
        return sourceAccount;
    }

    public void setSourceAccount(Account sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public Transaction(Double amount, LocalDate date, String description, Account sourceAccount, Account destinationAccount, User user) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.user = user;
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", date=" + date +
                ", description='" + description + '\'' +
                ", sourceAccount=" + (sourceAccount != null ? sourceAccount.getId() : "null") +
                ", destinationAccount=" + (destinationAccount != null ? destinationAccount.getId() : "null") +
                ", user=" + (user != null ? user.getId() : "null") +
                '}';
    }
}
