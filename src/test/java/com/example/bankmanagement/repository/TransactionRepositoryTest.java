package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.model.Transaction.TransactionType;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import com.example.bankmanagement.utils.ExtentReportManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

@DataJpaTest
public class TransactionRepositoryTest {

	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private UserRepository userRepository;

	private Transaction testTransaction;
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
		testUser.setDob(LocalDate.of(1990, 1, 1)); // Assuming LocalDate for date of birth
		testUser.setAddress("123 Main St");
		testUser.setAccountType("SAVINGS"); // Assuming accountType is a required field
		testUser = userRepository.save(testUser); // Save the user to the repository

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

		testTransaction = new Transaction();
		testTransaction.setAmount(BigDecimal.valueOf(100));
		testTransaction.setTransactionDate(LocalDateTime.now());
		testTransaction.setTransactionType(TransactionType.DEPOSIT);
		testTransaction.setDescription("Test Transaction");
		testTransaction.setSourceAccount(account1);
		testTransaction.setDestinationAccount(account2);
		testTransaction.setUser(testUser);
	}

	@Test
	public void testSaveTransaction() {

		runTest("Save Transaction", () -> {
			Transaction savedTransaction = transactionRepository.save(testTransaction);
			assertNotNull(savedTransaction.getId());
		});
	}

	@Test
	public void testFindBySourceAccountId() {

		runTest("Find By Source Account Id", () -> {
			Transaction savedTransaction = transactionRepository.save(testTransaction);
			List<Transaction> transactions = transactionRepository
					.findBySourceAccount_Id(savedTransaction.getSourceAccount().getId());
			assertFalse(transactions.isEmpty());
		});
	}

	@AfterAll
	public void flushReport() {
		ExtentReportManager.flushReports();
		System.out.println("✅ TransactionRepositoryTest report generated.");

	}

	private void runTest(String testName, Runnable testLogic) {
		test = ExtentReportManager.createTest(testName, ""
				+ "Repository Layer", "TransactionRepository");
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
