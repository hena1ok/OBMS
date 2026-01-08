package com.example.bankmanagement.Service;

import com.example.bankmanagement.model.*;
import com.example.bankmanagement.model.Transaction.TransactionStatus;
import com.example.bankmanagement.model.Transaction.TransactionType;
import com.example.bankmanagement.repository.AccountRepository;
import com.example.bankmanagement.repository.TellerRepository;
import com.example.bankmanagement.repository.TransactionRepository;
import com.example.bankmanagement.service.TransactionService;
import com.example.bankmanagement.utils.ExtentReportManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private AccountRepository accountRepository;
    private TellerRepository tellerRepository;
    private TransactionService transactionService;

    private ExtentReports extent;
    private ExtentTest test;

    @BeforeAll
    public void setupReport() {
        extent = ExtentReportManager.getInstance();
    }

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        accountRepository = mock(AccountRepository.class);
        tellerRepository = mock(TellerRepository.class);
        transactionService = new TransactionService(transactionRepository, accountRepository, tellerRepository);
    }

    @Test
    void testGetRecentTransactions() {
        runTest("Get Recent Transactions", () -> {
            List<Transaction> mockList = List.of(new Transaction(), new Transaction());
            when(transactionRepository.findTop10ByOrderByTransactionDateDesc()).thenReturn(mockList);

            List<Transaction> result = transactionService.getRecentTransactions();
            assertEquals(2, result.size());
            verify(transactionRepository).findTop10ByOrderByTransactionDateDesc();
        });
    }

    @Test
    void testGetTotalTransactions() {
        runTest("Get Total Transactions", () -> {
            when(transactionRepository.count()).thenReturn(25L);

            long total = transactionService.getTotalTransactions();
            assertEquals(25L, total);
            verify(transactionRepository).count();
        });
    }

    @Test
    void testCreateTransaction_Successful() {
        runTest("Create Transaction Successful", () -> {
            User user = new User();
            Account source = new Account();
            source.setId(1L);

            Account dest = new Account();
            dest.setId(2L);

            Teller teller = new Teller();
            teller.setTellerId(10L);

            when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
            when(accountRepository.findById(2L)).thenReturn(Optional.of(dest));
            when(tellerRepository.findById(10L)).thenReturn(Optional.of(teller));
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

            Transaction result = transactionService.createTransaction(
                    BigDecimal.valueOf(500), 1L, 2L,
                    TransactionType.TRANSFER, "Test transfer", user, 10L
            );

            assertEquals(BigDecimal.valueOf(500), result.getAmount());
            assertEquals(TransactionType.TRANSFER, result.getTransactionType());
            assertEquals(TransactionStatus.COMPLETED, result.getStatus());
            assertEquals(user, result.getUser());
            assertEquals(source, result.getSourceAccount());
            assertEquals(dest, result.getDestinationAccount());
            assertEquals(teller, result.getTeller());
            verify(transactionRepository).save(result);
        });
    }

    @Test
    void testCreateTransaction_WithoutDestinationOrTeller() {
        runTest("Create Transaction Without Destination Or Teller", () -> {
            User user = new User();
            Account source = new Account();
            source.setId(1L);

            when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

            Transaction result = transactionService.createTransaction(
                    BigDecimal.valueOf(100), 1L, null,
                    TransactionType.DEPOSIT, "No destination", user, null
            );

            assertEquals(TransactionType.DEPOSIT, result.getTransactionType());
            assertEquals(source, result.getSourceAccount());
            assertNull(result.getDestinationAccount());
            assertNull(result.getTeller());
        });
    }

    @Test
    void testCreateTransaction_SourceAccountNotFound() {
        runTest("Create Transaction - Source Account Not Found", () -> {
            when(accountRepository.findById(999L)).thenReturn(Optional.empty());

            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    transactionService.createTransaction(
                            BigDecimal.valueOf(100), 999L, null,
                            TransactionType.DEPOSIT, "fail", new User(), null));

            assertEquals("Source account not found", ex.getMessage());
        });
    }

    @Test
    void testCreateTransaction_DestinationAccountNotFound() {
        runTest("Create Transaction - Destination Account Not Found", () -> {
            Account source = new Account();
            source.setId(1L);

            when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
            when(accountRepository.findById(2L)).thenReturn(Optional.empty());

            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    transactionService.createTransaction(
                            BigDecimal.valueOf(200), 1L, 2L,
                            TransactionType.TRANSFER, "fail", new User(), null));

            assertEquals("Destination account not found", ex.getMessage());
        });
    }

    @Test
    void testCreateTransaction_TellerNotFound() {
        runTest("Create Transaction - Teller Not Found", () -> {
            Account source = new Account();
            source.setId(1L);

            when(accountRepository.findById(1L)).thenReturn(Optional.of(source));
            when(tellerRepository.findById(50L)).thenReturn(Optional.empty());

            Exception ex = assertThrows(IllegalArgumentException.class, () ->
                    transactionService.createTransaction(
                            BigDecimal.valueOf(150), 1L, null,
                            TransactionType.DEPOSIT, "fail", new User(), 50L));

            assertEquals("Teller not found", ex.getMessage());
        });
    }

    @Test
    void testGetTransactionsByAccount() {
        runTest("Get Transactions By Account", () -> {
            List<Transaction> mockList = List.of(new Transaction());
            when(transactionRepository.findBySourceAccount_Id(5L)).thenReturn(mockList);

            List<Transaction> result = transactionService.getTransactionsByAccount(5L);
            assertEquals(1, result.size());
        });
    }

    @Test
    void testGetTransactionsByUser() {
        runTest("Get Transactions By User", () -> {
            List<Transaction> mockList = List.of(new Transaction(), new Transaction());
            when(transactionRepository.findByUserId(3L)).thenReturn(mockList);

            List<Transaction> result = transactionService.getTransactionsByUser(3L);
            assertEquals(2, result.size());
        });
    }

    @Test
    void testGetTransactionById() {
        runTest("Get Transaction By ID", () -> {
            Transaction tx = new Transaction();
            tx.setId(9L);

            when(transactionRepository.findById(9L)).thenReturn(Optional.of(tx));

            Optional<Transaction> result = transactionService.getTransactionById(9L);
            assertTrue(result.isPresent());
            assertEquals(9L, result.get().getId());
        });
    }

    @AfterAll
    public void flushReport() {
    	ExtentReportManager.flushReports();
        System.out.println("✅ TransactionServiceTest report generated.");
    }

    private void runTest(String testName, Runnable testLogic) {
    	test = ExtentReportManager.createTest(testName, "Service Layer", "TransactionService");
        
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
