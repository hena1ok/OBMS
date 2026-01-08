package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Account;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import com.example.bankmanagement.utils.ExtentReportManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Account account1;
    private Account account2;
    private ExtentReports extent;
    private ExtentTest test;

   @BeforeAll
    public void setupReport() {
        extent = ExtentReportManager.getInstance(); // shared instance
    }


    @BeforeEach
    public void setUp() {
        // Create a test user with all required fields
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPhone("1234567890");
        testUser.setPassword("password123");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setDob(LocalDate.of(1990, 1, 1));  // Assuming LocalDate for date of birth
        testUser.setAddress("123 Main St");
        testUser.setAccountType("SAVINGS");  // Assuming accountType is a required field
        testUser = userRepository.save(testUser);  // Save the user to the repository

        // Create two test accounts
        account1 = new Account();
        account1.setAccountNumber("12345");
        account1.setAccountName("Account 1");
        account1.setAccountType("SAVINGS");
        account1.setBalance(new BigDecimal("1000"));
        account1.setUser(testUser);

        account2 = new Account();
        account2.setAccountNumber("67890");
        account2.setAccountName("Account 2");
        account2.setAccountType("CHECKING");
        account2.setBalance(new BigDecimal("2000"));
        account2.setUser(testUser);

        // Save accounts
        accountRepository.save(account1);
        accountRepository.save(account2);
    }

    @Test
    public void testExistsByAccountNumber_ShouldReturnTrue() {
    	runTest("Exists By Account Number Should Return True", () -> {
    	boolean exists = accountRepository.existsByAccountNumber("12345");
        assertTrue(exists);
    	});
 }

    @Test
    public void testExistsByAccountNumber_ShouldReturnFalse() {
    	runTest("Exists By Account Number Should Return False", () -> {
    	boolean exists = accountRepository.existsByAccountNumber("99999");
        assertFalse(exists);
    	});
}

    @Test
    public void testFindByUserId_ShouldReturnAccounts() {
    	runTest("Find By User Id Should Return Accounts", () -> {
    	List<Account> accounts = accountRepository.findByUserId(testUser.getId());
        assertEquals(2, accounts.size());
    	});
}

    @Test
    public void testFindByUser_ShouldReturnAccounts() {
    	runTest("Find By User Should Return Accounts", () -> {
    	List<Account> accounts = accountRepository.findByUser(testUser);
        assertEquals(2, accounts.size());
    	});
 }

    @Test
    public void testFindByUserUsernameContainingOrUserPhoneContaining_ShouldReturnAccount() {
    	runTest("Find By User Username Containing Or User Phone Containing Should Return Account", () -> {
    	List<Account> accounts = accountRepository.findByUserUsernameContainingOrUserPhoneContaining("testuser", "1234567890");
        assertEquals(2, accounts.size());
   
    	});
}

    @Test
    public void testFindByUserUsernameContainingOrUserPhoneContaining_ShouldReturnEmpty() {
    	runTest("Find By User Username Containing Or User Phone Containing Should Return Empty", () -> {
    	List<Account> accounts = accountRepository.findByUserUsernameContainingOrUserPhoneContaining("nonexistent", "0000000000");
        assertTrue(accounts.isEmpty());
    	});
 }
    @AfterAll
	public void flushReport() {
    	ExtentReportManager.flushReports();
		System.out.println("✅ AccountRepositoryTest report generated.");

	}



private void runTest(String testName, Runnable testLogic) {
	test = ExtentReportManager.createTest(testName, ""
			+ "Repository Layer", "AccountRepository");
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
