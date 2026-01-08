package com.example.bankmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a financial transaction in the banking system.
 */
@Entity
@Table(name = "transactions") // Explicitly specify the table name
public class Transaction implements Serializable {

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER
    }

    public enum TransactionStatus {
        PENDING, COMPLETED, FAILED
    }

    // Default status and date values
    private static final TransactionStatus DEFAULT_STATUS = TransactionStatus.PENDING;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount; // Monetary value of the transaction

    @NotNull(message = "Transaction date cannot be null")
    private LocalDateTime transactionDate; // Date and time of the transaction

    private String description; // Optional description of the transaction

    @ManyToOne(optional = false) // Mandatory source account
    @JoinColumn(name = "source_account_id", nullable = false)
    private Account sourceAccount;

    @ManyToOne // Nullable destination account for transfers
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;

    @ManyToOne(optional = false) // Mandatory user initiating the transaction
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne // Optional teller involved in the transaction
    @JoinColumn(name = "teller_id")
    private Teller teller;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType; // Type of transaction

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status", nullable = false)
    private TransactionStatus status; // Status of the transaction

    
    // Default constructor for JPA
    public Transaction() {
        this.transactionDate = LocalDateTime.now(); // Set to now by default
        this.status = DEFAULT_STATUS; // Default status
    }

 



	public static TransactionStatus getDefaultStatus() {
		return DEFAULT_STATUS;
	}

	// Constructor with all fields
    public Transaction(Long id,
                      @NotNull(message = "Amount cannot be null") @Positive(message = "Amount must be positive") BigDecimal amount,
                      @NotNull(message = "Transaction date cannot be null") LocalDateTime transactionDate,
                      String description, Account sourceAccount, Account destinationAccount, 
                      User user, Teller teller, 
                      TransactionType transactionType, TransactionStatus status) {
        this.id = id;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.user = user;
        this.teller = teller;
        this.transactionType = transactionType;
        this.status = status != null ? status : DEFAULT_STATUS; // Set to default if null
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
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

    public Teller getTeller() {
        return teller;
    }

    public void setTeller(Teller teller) {
        this.teller = teller;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus pending) {
        this.status = pending;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                ", description='" + description + '\'' +
                ", sourceAccountId=" + (sourceAccount != null ? sourceAccount.getId() : "null") +
                ", destinationAccountId=" + (destinationAccount != null ? destinationAccount.getId() : "null") +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", tellerId=" + (teller != null ? teller.getTellerId() : "null") +
                ", transactionType=" + transactionType +
                ", status=" + status +
                '}';
    }
}
