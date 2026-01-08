package com.example.bankmanagement.Service;

import com.example.bankmanagement.model.Loan;
import com.example.bankmanagement.model.LoanStatus;
import com.example.bankmanagement.model.LoanType;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.LoanRepository;
import com.example.bankmanagement.service.LoanService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.bankmanagement.utils.ExtentReportManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private Loan testLoan;
    private User testUser;
    
    private ExtentReports extent;
    private ExtentTest test;

   @BeforeAll
    public void setupReport() {
        extent = ExtentReportManager.getInstance(); // shared instance
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mock objects

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john_doe");

        testLoan = new Loan();
        testLoan.setId(1L);
        testLoan.setAmount(BigDecimal.valueOf(10000));
        testLoan.setInterestRate(5.5);
        testLoan.setTerm(12);
        testLoan.setDuration(12);
        testLoan.setLoanType(LoanType.PERSONAL_LOAN);
        testLoan.setStatus(LoanStatus.PENDING);
        testLoan.setUser(testUser);
    }

    @Test
    public void testApplyForLoan_Success() {
    	runTest("Apply For Loan Success", () -> {

    	when(loanRepository.save(any(Loan.class))).thenReturn(testLoan);

        Loan appliedLoan = loanService.applyForLoan(testLoan, testUser);

        assertNotNull(appliedLoan);
        assertEquals(LoanStatus.PENDING, appliedLoan.getStatus());
        assertEquals(testUser, appliedLoan.getUser());
        verify(loanRepository, times(1)).save(testLoan);
    	 });
    	}

    @Test
    public void testGetAllLoanTypes_Success() {
    	runTest("Get All Loan Types Success", () -> {

    	List<LoanType> loanTypes = loanService.getAllLoanTypes();

        assertNotNull(loanTypes);
        assertTrue(loanTypes.containsAll(Arrays.asList(LoanType.values())));
    	 });
    	}

    @Test
    public void testGetAllLoans_Success() {
    	runTest("Get All Loans Success", () -> {

    	when(loanRepository.findAll()).thenReturn(List.of(testLoan));

        List<Loan> loans = loanService.getAllLoans();

        assertNotNull(loans);
        assertEquals(1, loans.size());
        assertEquals(testLoan.getId(), loans.get(0).getId());
    	 });
    	}

    @Test
    public void testGetLoanById_Found() {
    	runTest("Get Loan By Id Found", () -> {

    	when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));

        Loan foundLoan = loanService.getLoanById(1L);

        assertNotNull(foundLoan);
        assertEquals(testLoan.getId(), foundLoan.getId());
    	 });
    	}

    @Test
    public void testGetLoanById_NotFound() {
    	runTest("Get Loan By Id Not Found", () -> {

    	when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        Loan foundLoan = loanService.getLoanById(1L);

        assertNull(foundLoan);
    	 });
    	}

    @Test
    public void testAcceptLoan_Success() {
    	runTest("Accept Loan Success", () -> {

    	when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));

        loanService.acceptLoan(1L);

        assertEquals(LoanStatus.APPROVED, testLoan.getStatus());
        verify(loanRepository, times(1)).save(testLoan);
    	 });
    	}

    @Test
    public void testRejectLoan_Success() {
    	runTest("Reject Loan Success", () -> {

    	when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));

        loanService.rejectLoan(1L);

        assertEquals(LoanStatus.REJECTED, testLoan.getStatus());
        verify(loanRepository, times(1)).save(testLoan);
    	 });
    	}

    @Test
    public void testGetTotalLoans_Success() {
    	runTest("Get Total Loans Success", () -> {

    	when(loanRepository.count()).thenReturn(5L);

        long totalLoans = loanService.getTotalLoans();

        assertEquals(5L, totalLoans);
    	 });
    	}

    @Test
    public void testUpdateLoan_Success() {
    	runTest("Update Loan Success", () -> {

    	Loan updatedLoan = new Loan();
        updatedLoan.setAmount(BigDecimal.valueOf(15000));
        updatedLoan.setInterestRate(4.5);
        updatedLoan.setTerm(24);
        updatedLoan.setDuration(24);
        updatedLoan.setLoanType(LoanType.HOME_LOAN);
        updatedLoan.setStatus(LoanStatus.APPROVED);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(testLoan));

        loanService.updateLoan(1L, updatedLoan);

        assertEquals(BigDecimal.valueOf(15000), testLoan.getAmount());
        assertEquals(4.5, testLoan.getInterestRate());
        assertEquals(24, testLoan.getTerm());
        assertEquals(24, testLoan.getDuration());
        assertEquals(LoanType.HOME_LOAN, testLoan.getLoanType());
        assertEquals(LoanStatus.APPROVED, testLoan.getStatus());
        verify(loanRepository, times(1)).save(testLoan);
    	 });
    	}

    @Test
    public void testUpdateLoan_NotFound() {
    	runTest("Update Loan Not Found", () -> {

    	Loan updatedLoan = new Loan();
        when(loanRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            loanService.updateLoan(2L, updatedLoan);
        });

        assertEquals("Loan with ID 2 does not exist.", exception.getMessage());
    });
}
    private void runTest(String testName, Runnable testLogic) {
    	test = ExtentReportManager.createTest(testName, "Service Layer", "LoanService");
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
    @AfterAll
	public void flushReport() {
    	ExtentReportManager.flushReports();
		System.out.println("✅ LoanServiceTest report generated.");

	}
}
