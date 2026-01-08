package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Loan;
import com.example.bankmanagement.model.LoanStatus;
import com.example.bankmanagement.model.LoanType;
import com.example.bankmanagement.model.User;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import com.example.bankmanagement.utils.ExtentReportManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository; // Add UserRepository

    private Loan testLoan;
    
    private ExtentReports extent;
    private ExtentTest test;

   @BeforeAll
    public void setupReport() {
        extent = ExtentReportManager.getInstance(); // shared instance
    }


    @BeforeEach
    public void setUp() {
        // Create and save the User entity
        User user = new User();
        user = new User("testuser", "password123", "testuser@example.com", "John", "Doe",
                "1234567890", "123 Main St", LocalDate.of(1990, 1, 1), "SAVINGS");
        user = userRepository.save(user); // Persist the User

        // Create the Loan entity and associate it with the saved User
        testLoan = new Loan();
        testLoan.setAmount(BigDecimal.valueOf(5000.0));
        testLoan.setInterestRate(6.5);
        testLoan.setTerm(24);
        testLoan.setDuration(24);
        testLoan.setLoanType(LoanType.PERSONAL_LOAN);
        testLoan.setLoanPurpose("Home Renovation");
        testLoan.setStatus(LoanStatus.PENDING);
        testLoan.setStartDate(LocalDate.now());
        testLoan.setUser(user); // Associate the persisted User
    }

    @Test
    public void testSaveLoan() {
    	runTest("Save Loan", () -> {
    	Loan savedLoan = loanRepository.save(testLoan);

        assertNotNull(savedLoan.getId(), "Loan ID should not be null after saving.");
        assertEquals(testLoan.getAmount(), savedLoan.getAmount(), "Loan amounts should match.");
    	});
 }

    @Test
    public void testFindLoanById() {
    	runTest("Find Loan By Id", () -> {
    	Loan savedLoan = loanRepository.save(testLoan);

        Optional<Loan> foundLoan = loanRepository.findById(savedLoan.getId());

        assertTrue(foundLoan.isPresent(), "Loan should be found by ID.");
        assertEquals(savedLoan.getId(), foundLoan.get().getId(), "Loan IDs should match.");
    	});
 }

    @Test
    public void testDeleteLoan() {
    	runTest("Delete Loan", () -> {
    	Loan savedLoan = loanRepository.save(testLoan);

        loanRepository.delete(savedLoan);

        Optional<Loan> foundLoan = loanRepository.findById(savedLoan.getId());

        assertFalse(foundLoan.isPresent(), "Loan should be deleted.");
    	});
}

    @Test
    public void testUpdateLoan() {
    	runTest("Update Loan", () -> {
    	Loan savedLoan = loanRepository.save(testLoan);

        savedLoan.setInterestRate(5.0);
        Loan updatedLoan = loanRepository.save(savedLoan);

        assertEquals(5.0, updatedLoan.getInterestRate(), "Interest rate should be updated.");
    	});
}
    @AfterAll
	public void flushReport() {
    	ExtentReportManager.flushReports();
		System.out.println("✅ LoanRepositoryTest report generated.");

	}



private void runTest(String testName, Runnable testLogic) {
	test = ExtentReportManager.createTest(testName, ""
			+ "Repository Layer", "LoanRepository");
		try {
			testLogic.run();
			test.pass("✅ Test passed successfully");
		} catch (AssertionError e) {
			test.fail("❌ Assertion failed: " + e.getMessage());
			throw e;
		} catch (Exception e) {
			test.fail("❗ Unexpected exception: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
