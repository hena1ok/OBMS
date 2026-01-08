package com.example.bankmanagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount; // Loan amount, now using BigDecimal for precision in monetary values

    @Column(nullable = false)
    private double interestRate; // Interest rate as a percentage

    @Column(nullable = false)
    private int term; // Loan term (e.g., months or years)

    @Column(nullable = false)
    private int duration; // Loan duration in months/years, refined for clarity

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType; // Loan type (e.g., personal, mortgage)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status; // Loan status (e.g., PENDING, APPROVED, REJECTED)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Reference to the User who applied for the loan

    @Column(nullable = false)
    private LocalDate startDate; // Start date of the loan

    @Column(nullable = true)
    private LocalDate endDate; // Optional end date, if the loan has a term to complete

    @Column(nullable = false)
    private String loanPurpose;

    // Constructors
    public Loan() {}

    public Loan(BigDecimal amount, double interestRate, int term, int duration, LoanType loanType, User user) {
        this.amount = amount;
        this.interestRate = interestRate;
        this.term = term;
        this.duration = duration;
        this.loanType = loanType;
        this.user = user;
        this.startDate = LocalDate.now(); // Default start date to current date
        this.status = LoanStatus.PENDING; // Default status to PENDING
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

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

	public String getLoanPurpose() {
		return loanPurpose;
	}

	public void setLoanPurpose(String loanPurpose) {
		this.loanPurpose = loanPurpose;
	}

    // Additional utility methods can be added here, such as calculating interest or remaining balance
}
