package com.example.bankmanagement.service;

import com.example.bankmanagement.model.Loan;
import com.example.bankmanagement.model.LoanStatus;
import com.example.bankmanagement.model.LoanType;
import com.example.bankmanagement.model.User; // Import User model
import com.example.bankmanagement.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    /**
     * Applies for a loan and sets initial properties.
     * @param loan The loan to apply for.
     * @param user The user applying for the loan.
     * @return The saved loan entity.
     */
    public Loan applyForLoan(Loan loan, User user) {
        // Debugging logs
        System.out.println("Applying for loan: " + loan);
        System.out.println("Current user: " + user);

        // Set initial properties for the loan
        loan.setStartDate(LocalDate.now());
        loan.setUser(user); // Set the user associated with the loan
        loan.setStatus(LoanStatus.PENDING); // Default status

        validateLoanApplication(loan); // Validate loan application

        return loanRepository.save(loan); // Save the loan to the repository
    }

    public List<LoanType> getAllLoanTypes() {
        return Arrays.asList(LoanType.values());
    }

    /**
     * Retrieves all loans from the repository.
     * @return List of all loans.
     */
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    /**
     * Retrieves a loan by its ID.
     * @param id The ID of the loan.
     * @return The loan if found, or null.
     */
    public Loan getLoanById(Long id) {
        return loanRepository.findById(id).orElse(null);
    }

    /**
     * Accepts a loan by marking it as accepted.
     * @param loanId The ID of the loan to accept.
     */
    public void acceptLoan(Long loanId) {
        Loan loan = getLoanById(loanId);
        if (loan != null) {
            loan.setStatus(LoanStatus.APPROVED); // Mark loan as accepted
            loanRepository.save(loan); // Save changes to the repository
        } else {
            throw new IllegalArgumentException("Loan not found with ID: " + loanId);
        }
    }

    /**
     * Rejects a loan by marking it as rejected.
     * @param loanId The ID of the loan to reject.
     */
    public void rejectLoan(Long loanId) {
        Loan loan = getLoanById(loanId);
        if (loan != null) {
            loan.setStatus(LoanStatus.REJECTED); // Mark loan as rejected
            loanRepository.save(loan); // Save changes to the repository
        } else {
            throw new IllegalArgumentException("Loan not found with ID: " + loanId);
        }
    }

    /**
     * Validates the loan application before saving.
     * @param loan The loan to validate.
     */
    private void validateLoanApplication(Loan loan) {
        if (loan.getAmount() == null || loan.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Loan amount must be positive.");
        }
        if (loan.getInterestRate() <= 0) {
            throw new IllegalArgumentException("Interest rate must be positive.");
        }
        if (loan.getTerm() <= 0) {
            throw new IllegalArgumentException("Loan term must be positive.");
        }
        if (loan.getDuration() <= 0) {
            throw new IllegalArgumentException("Loan duration must be positive.");
        }
        if (loan.getUser() == null || loan.getUser().getId() == null) {
            throw new IllegalArgumentException("User must be associated with the loan application.");
        }
        // Additional validation rules can be added here
    }


    public long getTotalLoans() {
        return loanRepository.count(); // Directly use count from the repository
    }

    public void updateLoan(Long id, Loan loan) {
        // Check if the loan exists
        Optional<Loan> existingLoanOpt = loanRepository.findById(id);
        if (!existingLoanOpt.isPresent()) {
            throw new IllegalArgumentException("Loan with ID " + id + " does not exist.");
        }

        // Get the existing loan
        Loan existingLoan = existingLoanOpt.get();

        // Update the existing loan with new values
        existingLoan.setAmount(loan.getAmount());
        existingLoan.setInterestRate(loan.getInterestRate());
        existingLoan.setTerm(loan.getTerm());
        existingLoan.setDuration(loan.getDuration());
        existingLoan.setLoanType(loan.getLoanType());
        existingLoan.setStatus(loan.getStatus());

        // Save the updated loan back to the repository
        loanRepository.save(existingLoan);
    }
}
