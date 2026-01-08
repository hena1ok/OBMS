package com.example.bankmanagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Map;

@Entity
public class ATM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Location is required")
    private String location;  // ATM's physical location

    @Column(nullable = false)
    @NotNull(message = "Cash available is required")
    @Min(value = 0, message = "Cash available must be non-negative")
    private Double cashAvailable;  // Total cash available in the ATM

    @Column(nullable = false)
    @NotNull(message = "Max withdrawal per transaction is required")
    @Min(value = 0, message = "Max withdrawal must be non-negative")
    private Double maxWithdrawalPerTransaction;  // Maximum allowed per withdrawal

    @Column(nullable = false)
    @NotNull(message = "Daily withdrawal limit is required")
    @Min(value = 0, message = "Daily withdrawal limit must be non-negative")
    private Double dailyWithdrawalLimit;  // Daily withdrawal limit for the ATM

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;  // Operational status

    @ElementCollection
    @CollectionTable(name = "atm_cash_denominations", joinColumns = @JoinColumn(name = "atm_id"))
    @MapKeyColumn(name = "denomination")
    @Column(name = "count")
    private Map<Integer, Integer> cashDenominations;  // Breakdown of cash by denominations (e.g., $10, $20)

    // Default constructor
    public ATM() {
    	
    }
    

    // Constructor for creating a new ATM
    public ATM(String location, Double cashAvailable, Double maxWithdrawalPerTransaction, Double dailyWithdrawalLimit, Status status, Map<Integer, Integer> cashDenominations) {
        this.location = location;
        this.cashAvailable = cashAvailable;
        this.maxWithdrawalPerTransaction = maxWithdrawalPerTransaction;
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
        this.status = status;
        this.cashDenominations = cashDenominations;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getCashAvailable() {
        return cashAvailable;
    }

    public void setCashAvailable(Double cashAvailable) {
        this.cashAvailable = cashAvailable;
    }

    public Double getMaxWithdrawalPerTransaction() {
        return maxWithdrawalPerTransaction;
    }

    public void setMaxWithdrawalPerTransaction(Double maxWithdrawalPerTransaction) {
        this.maxWithdrawalPerTransaction = maxWithdrawalPerTransaction;
    }

    public Double getDailyWithdrawalLimit() {
        return dailyWithdrawalLimit;
    }

    public void setDailyWithdrawalLimit(Double dailyWithdrawalLimit) {
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Map<Integer, Integer> getCashDenominations() {
        return cashDenominations;
    }

    public void setCashDenominations(Map<Integer, Integer> cashDenominations) {
        this.cashDenominations = cashDenominations;
    }

    @Override
    public String toString() {
        return String.format("ATM[id=%d, location='%s', cashAvailable=%.2f, maxWithdrawalPerTransaction=%.2f, dailyWithdrawalLimit=%.2f, status=%s, cashDenominations=%s]",
                id, location, cashAvailable, maxWithdrawalPerTransaction, dailyWithdrawalLimit, status, cashDenominations);
    }

    // Enum for ATM status
    public enum Status {
        ONLINE,
        OFFLINE,
        MAINTENANCE
    }
}
