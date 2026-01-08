package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Transaction;
import com.example.bankmanagement.service.TransactionService;
import com.example.bankmanagement.service.UserService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.bankmanagement.utils.ExtentReportManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionControllerTest {

    @Mock private TransactionService transactionService;
    @Mock private UserService userService;
    @Mock private Model model;
    @Mock private RedirectAttributes redirectAttributes;

    private ExtentReports extent;
    private ExtentTest test;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public void setupReport() {
        extent = ExtentReportManager.getInstance();
    }

    @AfterAll
    public void flushReport() {
    	ExtentReportManager.flushReports();
        System.out.println("✅ TransactionControllerTest report generated.");
    }

    @Test
    public void testShowCreateTransactionForm() {
        runTest("Show Create Transaction Form", () -> {
            String viewName = transactionController.showCreateTransactionForm(null, model);
            assertEquals("transaction/create", viewName);
            verify(model, times(1)).addAttribute(eq("transaction"), any(Transaction.class));
        });
    }

    @Test
    public void testCreateTransaction_Success() {
        runTest("Create Transaction Success", () -> {
            when(userService.findById(anyLong()))
                .thenReturn(java.util.Optional.of(new com.example.bankmanagement.model.User()));
            when(transactionService.createTransaction(any(), anyLong(), anyLong(), any(), any(), any(), anyLong()))
                .thenReturn(new Transaction());

            String viewName = transactionController.createTransaction(
                BigDecimal.valueOf(100), 1L, null, "DEPOSIT", "Test Description", null, redirectAttributes);

            assertEquals("redirect:/transactions/create", viewName);
            verify(redirectAttributes, times(1)).addFlashAttribute(eq("message"), anyString());
        });
    }

    @Test
    public void testCreateTransaction_Failure() {
        runTest("Create Transaction Failure", () -> {
            String viewName = transactionController.createTransaction(
                BigDecimal.valueOf(-100), 1L, null, "DEPOSIT", "Test Description", null, redirectAttributes);

            assertEquals("redirect:/transactions/create", viewName);
            verify(redirectAttributes, times(1)).addFlashAttribute(eq("error"), anyString());
        });
    }

private void runTest(String testName, Runnable testLogic) {
	test = ExtentReportManager.createTest(testName, ""
			+ "Controller Layer", "TransactionController");
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
