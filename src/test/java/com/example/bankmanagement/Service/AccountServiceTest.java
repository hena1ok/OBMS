package com.example.bankmanagement.Service;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.AccountRepository;
import com.example.bankmanagement.repository.UserRepository;
import com.example.bankmanagement.service.AccountService;
import com.example.bankmanagement.utils.ExtentReportManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountServiceTest {

    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private AccountService accountService;
    
    private ExtentReports extent;
    private ExtentTest test;


    @BeforeAll
    public void setupReport() {
        extent = ExtentReportManager.getInstance();
    }
    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        userRepository = mock(UserRepository.class);
        accountService = new AccountService(accountRepository, userRepository);
    }

    @Test
    void testGetAllAccounts() {
        runTest("testGetAllAccounts", () -> {
            List<Account> mockAccounts = List.of(new Account(), new Account());
            when(accountRepository.findAll()).thenReturn(mockAccounts);

            List<Account> result = accountService.getAllAccounts();
            assertEquals(2, result.size());
            verify(accountRepository).findAll();
        });
    }

    @Test
    void testGetAccountById_ValidId() {
        runTest("testGetAccountById_ValidId", () -> {
            Account mockAccount = new Account();
            when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));

            Account result = accountService.getAccountById(1L);
            assertEquals(mockAccount, result);
        });
    }

    @Test
    void testGetAccountById_InvalidId() {
        runTest("testGetAccountById_InvalidId", () -> {
            when(accountRepository.findById(1L)).thenReturn(Optional.empty());

            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> accountService.getAccountById(1L));
            assertEquals("Account not found with id: 1", exception.getMessage());
        });
    }

    @Test
    void testCreateAccount_ValidUserId() {
        runTest("testCreateAccount_ValidUserId", () -> {
            User mockUser = new User();
            mockUser.setId(100L);

            Account accountInput = new Account();
            accountInput.setAccountName("Savings");
            accountInput.setAccountType("Personal");
            accountInput.setBalance(BigDecimal.valueOf(1000));

            when(userRepository.findById(100L)).thenReturn(Optional.of(mockUser));
            when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Account result = accountService.createAccount(accountInput, 100L);

            assertEquals(mockUser, result.getUser());
            assertNotNull(result.getAccountNumber());
            assertEquals(LocalDate.now(), result.getDateOpened());
            verify(accountRepository).save(result);
        });
    }

    @Test
    void testCreateAccount_InvalidUserId() {
        runTest("testCreateAccount_InvalidUserId", () -> {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> accountService.createAccount(new Account(), 999L));
            assertEquals("User not found with id: 999", exception.getMessage());
        });
    }

    @Test
    void testUpdateAccount_Valid() {
        runTest("testUpdateAccount_Valid", () -> {
            Account existing = new Account();
            existing.setId(1L);
            existing.setBalance(BigDecimal.valueOf(100));
            existing.setAccountName("Old Name");
            existing.setAccountType("Old Type");

            Account updated = new Account();
            updated.setAccountName("New Name");
            updated.setAccountType("New Type");
            updated.setBalance(BigDecimal.valueOf(500));

            when(accountRepository.findById(1L)).thenReturn(Optional.of(existing));
            when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

            Account result = accountService.updateAccount(1L, updated);

            assertEquals("New Name", result.getAccountName());
            assertEquals("New Type", result.getAccountType());
            assertEquals(BigDecimal.valueOf(500), result.getBalance());
        });
    }

    @Test
    void testUpdateAccount_NegativeBalance() {
        runTest("testUpdateAccount_NegativeBalance", () -> {
            Account existing = new Account();
            when(accountRepository.findById(1L)).thenReturn(Optional.of(existing));

            Account updateAttempt = new Account();
            updateAttempt.setBalance(BigDecimal.valueOf(-100));

            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> accountService.updateAccount(1L, updateAttempt));
            assertEquals("Balance must be positive", ex.getMessage());
        });
    }

    @Test
    void testDeleteAccount_ValidId() {
        runTest("testDeleteAccount_ValidId", () -> {
            when(accountRepository.existsById(1L)).thenReturn(true);
            accountService.deleteAccount(1L);
            verify(accountRepository).deleteById(1L);
        });
    }

    @Test
    void testDeleteAccount_InvalidId() {
        runTest("testDeleteAccount_InvalidId", () -> {
            when(accountRepository.existsById(1L)).thenReturn(false);

            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> accountService.deleteAccount(1L));
            assertEquals("Account not found with id: 1", ex.getMessage());
        });
    }

    @Test
    void testFindAccountsByUser_ExistingUser() {
        runTest("testFindAccountsByUser_ExistingUser", () -> {
            User mockUser = new User();
            mockUser.setId(10L);
            when(userRepository.findByUsername("john")).thenReturn(Optional.of(mockUser));

            List<Account> accounts = List.of(new Account(), new Account());
            when(accountRepository.findByUserId(10L)).thenReturn(accounts);

            List<Account> result = accountService.findAccountsByUser("john");

            assertEquals(2, result.size());
        });
    }

    @Test
    void testFindAccountsByUser_UserNotFound() {
        runTest("testFindAccountsByUser_UserNotFound", () -> {
            when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
            List<Account> result = accountService.findAccountsByUser("nonexistent");
            assertTrue(result.isEmpty());
        });
    }

    @Test
    void testGenerateAccountNumber() {
        runTest("testGenerateAccountNumber", () -> {
            String accNum = accountService.generateAccountNumber();
            assertEquals(10, accNum.length());
            assertTrue(accNum.matches("\\d{10}"));
        });
    }

    @Test
    void testSearchAccounts() {
        runTest("testSearchAccounts", () -> {
            List<Account> mockAccounts = List.of(new Account());
            when(accountRepository.findByUserUsernameContainingOrUserPhoneContaining("john", "john"))
                    .thenReturn(mockAccounts);

            List<Account> result = accountService.searchAccounts("john");
            assertEquals(1, result.size());
        });
    }

    @Test
    void testFindAccountsByUserId() {
        runTest("testFindAccountsByUserId", () -> {
            List<Account> accounts = List.of(new Account());
            when(accountRepository.findByUserId(5L)).thenReturn(accounts);

            List<Account> result = accountService.findAccountsByUserId(5L);
            assertEquals(1, result.size());
        });
    }

    @Test
    void testFindById() {
        runTest("testFindById", () -> {
            Account mock = new Account();
            when(accountRepository.findById(77L)).thenReturn(Optional.of(mock));

            Optional<Account> result = accountService.findById(77L);
            assertTrue(result.isPresent());
        });
    }

    @Test
    void testFindByUser() {
        runTest("testFindByUser", () -> {
            User mockUser = new User();
            List<Account> accounts = List.of(new Account());
            when(accountRepository.findByUser(mockUser)).thenReturn(accounts);

            List<Account> result = accountService.findByUser(mockUser);
            assertEquals(1, result.size());
        });
    }

    @Test
    void testSave() {
        runTest("testSave", () -> {
            Account account = new Account();
            accountService.save(account);
            verify(accountRepository).save(account);
        });
    }

    @AfterAll
    public void flushReport() {
    	 ExtentReportManager.flushReports();
        System.out.println("✅ TransactionServiceTest report generated.");
    }

    private void runTest(String testName, Runnable testLogic) {
    	test = ExtentReportManager.createTest(testName, "Service Layer", "AccountService");
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
