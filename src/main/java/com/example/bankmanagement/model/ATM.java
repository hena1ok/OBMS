package com.example.bankmanagement.model;

import jakarta.persistence.*;
import java.util.Map;

@Entity
public class ATM {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String location;  // ATM's physical location

    @Column(nullable = false)
    private Double cashAvailable;  // Total cash available in the ATM

    @Column(nullable = false)
    private Double maxWithdrawalPerTransaction;  // Maximum allowed per withdrawal

    @Column(nullable = false)
    private Double dailyWithdrawalLimit;  // Daily withdrawal limit for the ATM

    @Column(nullable = false)
    private String status;  // Operational status: "online", "offline", "maintenance"

    @ElementCollection
    @CollectionTable(name = "atm_cash_denominations", joinColumns = @JoinColumn(name = "atm_id"))
    @MapKeyColumn(name = "denomination")
    @Column(name = "count")
    private Map<Integer, Integer> cashDenominations;  // Breakdown of cash by denominations (e.g., $10, $20)

    // Constructors, getters, setters

    public ATM() {
    }

    public ATM(String location, Double cashAvailable, Double maxWithdrawalPerTransaction, Double dailyWithdrawalLimit, String status, Map<Integer, Integer> cashDenominations) {
        this.location = location;
        this.cashAvailable = cashAvailable;
        this.maxWithdrawalPerTransaction = maxWithdrawalPerTransaction;
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
        this.status = status;
        this.cashDenominations = cashDenominations;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
        return "ATM{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", cashAvailable=" + cashAvailable +
                ", maxWithdrawalPerTransaction=" + maxWithdrawalPerTransaction +
                ", dailyWithdrawalLimit=" + dailyWithdrawalLimit +
                ", status='" + status + '\'' +
                ", cashDenominations=" + cashDenominations +
                '}';
    }
}
