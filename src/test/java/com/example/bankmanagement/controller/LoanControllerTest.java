package com.example.bankmanagement.controller;

import com.example.bankmanagement.controller.AccountControllerTest.ThrowingRunnable;
import com.example.bankmanagement.model.Loan;
import com.example.bankmanagement.model.LoanType;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.security.CustomUserDetails;
import com.example.bankmanagement.service.LoanService;
import com.example.bankmanagement.service.UserService;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.bankmanagement.utils.ExtentReportManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoanControllerTest {

	@Mock
	private LoanService loanService;

	@Mock
	private UserService userService;

	@InjectMocks
	private LoanController loanController;

	private MockMvc mockMvc;
	private User testUser;
	private CustomUserDetails userDetails;
	private Loan testLoan;

	private ExtentReports extent;
	private ExtentTest test;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(loanController).build();

		testUser = new User();
		testUser.setId(1L);
		testUser.setEmail("user@example.com");

		userDetails = new CustomUserDetails(testUser);

		testLoan = new Loan();
		testLoan.setId(1L);
		testLoan.setAmount(BigDecimal.valueOf(10000));
		testLoan.setUser(testUser);
	}

	@BeforeAll
	public void setupReport() {
		extent = ExtentReportManager.getInstance(); // shared instance
	}

	@Test
	public void testListLoans() throws Exception {
		runTest("List Loans", () -> {
		when(loanService.getAllLoans()).thenReturn(Collections.singletonList(testLoan));

		
			mockMvc.perform(get("/loan/list")).andExpect(status().isOk()).andExpect(view().name("loan/loan_list"))
					.andExpect(model().attributeExists("loans"));
	
		});

		}

	@Test
	public void testShowLoanForm() throws Exception {
		runTest("Show Loan Form", () -> {
		when(loanService.getAllLoanTypes()).thenReturn(Arrays.asList(LoanType.HOME_LOAN, LoanType.CAR_LOAN));

	
			mockMvc.perform(get("/loan/apply")).andExpect(status().isOk()).andExpect(view().name("loan/loan_form"))
					.andExpect(model().attributeExists("loanTypes")).andExpect(model().attributeExists("loan"));
		
		});

		}

	@Test
	@WithMockUser
	public void testApplyForLoan_AsUser_Success() throws Exception {
		runTest("Apply For Loan As User Success", () -> {
		when(loanService.applyForLoan(any(Loan.class), eq(testUser))).thenReturn(testLoan);

	
			mockMvc.perform(post("/loan/apply").flashAttr("loan", testLoan)).andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/loan/list")).andExpect(flash().attributeExists("successMessage"));
		

		verify(loanService).applyForLoan(any(Loan.class), eq(testUser));
		});

		}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void testApplyForLoan_AsAdmin_SelectedUser() throws Exception {
		runTest("Apply For Loan As Admin Selected User", () -> {
		User another = new User();
		another.setEmail("other@example.com");
		Loan loanForm = new Loan();
		loanForm.setUser(another);

		when(userService.findByEmail("other@example.com")).thenReturn(Optional.of(another));
		when(loanService.applyForLoan(any(Loan.class), eq(another))).thenReturn(testLoan);

		
			mockMvc.perform(post("/loan/apply").flashAttr("loan", loanForm)).andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/loan/list"));
	
		verify(loanService).applyForLoan(any(Loan.class), eq(another));
		});

		}

	@Test
	@WithMockUser
	public void testApplyForLoan_Failure() throws Exception {
		runTest("Apply For Loan Failure", () -> {
		when(loanService.applyForLoan(any(Loan.class), any(User.class)))
				.thenThrow(new RuntimeException("Insufficient credit"));
		when(loanService.getAllLoanTypes()).thenReturn(Arrays.asList(LoanType.HOME_LOAN, LoanType.CAR_LOAN));

	
			mockMvc.perform(post("/loan/apply").flashAttr("loan", testLoan)).andExpect(status().isOk())
					.andExpect(view().name("loan/loan_form")).andExpect(model().attributeExists("errorMessage"))
					.andExpect(model().attributeExists("loanTypes"));
		
		});

		}

	@Test
	public void testApproveLoan() throws Exception {
		runTest("Approve Loan", () -> {
		doNothing().when(loanService).acceptLoan(1L);

	
			mockMvc.perform(post("/loan/approve/1")).andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/loan/list"));
	
		});

		}

	@Test
	public void testRejectLoan() throws Exception {
		runTest("Reject Loan", () -> {
		doNothing().when(loanService).rejectLoan(1L);

	
			mockMvc.perform(post("/loan/reject/1")).andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/loan/list"));
		
		});

		}

	@Test
	public void testEditLoanForm() throws Exception {
		runTest("Edit Loan Form", () -> {
		when(loanService.getLoanById(1L)).thenReturn(testLoan);
		when(loanService.getAllLoanTypes()).thenReturn(Arrays.asList(LoanType.HOME_LOAN, LoanType.CAR_LOAN));

		
			mockMvc.perform(get("/loan/edit/1")).andExpect(status().isOk()).andExpect(view().name("loan/loan_form"))
					.andExpect(model().attributeExists("loan")).andExpect(model().attributeExists("loanTypes"));
		
		});

		}

	@Test
	public void testUpdateLoan_Success() throws Exception {
		runTest("Update Loan Success", () -> {
		doNothing().when(loanService).updateLoan(eq(1L), any(Loan.class));

		
			mockMvc.perform(post("/loan/edit/1").flashAttr("loan", testLoan)).andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/loan/list"));
		
		});

		}

	@Test
	public void testUpdateLoan_Failure() throws Exception {
		runTest("Update Loan Failure", () -> {
		doThrow(new RuntimeException("Validation error")).when(loanService).updateLoan(eq(1L), any(Loan.class));
		when(loanService.getAllLoanTypes()).thenReturn(Arrays.asList(LoanType.HOME_LOAN));

		
			mockMvc.perform(post("/loan/edit/1").flashAttr("loan", testLoan)).andExpect(status().isOk())
					.andExpect(view().name("loan/loan_form")).andExpect(model().attributeExists("errorMessage"))
					.andExpect(model().attributeExists("loanTypes"));
		
		
		});

		}

	@Test
	public void testViewLoanDetails() throws Exception {
		runTest("View Loan Details", () -> {
		when(loanService.getLoanById(1L)).thenReturn(testLoan);

		try {
			mockMvc.perform(get("/loan/details/1")).andExpect(status().isOk()).andExpect(view().name("loan/loan_details"))
					.andExpect(model().attributeExists("loan"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		});

		}

	@AfterAll
	public void flushReport() {
		ExtentReportManager.flushReports();
		System.out.println("✅ LoanControllerTest report generated.");

	}


	  @FunctionalInterface
	    interface ThrowingRunnable {
	        void run() throws Exception;
	    }


	    private void runTest(String testName, ThrowingRunnable testLogic) {
	    	test = ExtentReportManager.createTest(testName, ""
					+ "Controller Layer", "LoanController");
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
