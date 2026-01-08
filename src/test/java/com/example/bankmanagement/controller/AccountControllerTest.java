package com.example.bankmanagement.controller;

import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.AccountService;
import com.example.bankmanagement.service.UserService;
import com.example.bankmanagement.utils.ExtentReportManager;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private UserService userService;

    private User testUser;
    private Account testAccount;

    private ExtentReports extent;
    private ExtentTest test;

    @BeforeAll
    public void setupReport() {
        extent = ExtentReportManager.getInstance();
    }

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john_doe");

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setUser(testUser);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testListAccounts_AsAdmin_ShouldReturnAllAccounts() {
        runTest("List Accounts As Admin Should Return All Accounts", () -> {
            when(accountService.findAll()).thenReturn(List.of(testAccount));

            mockMvc.perform(get("/account/account_list"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("accounts"))
                    .andExpect(view().name("account/account_list"));
        });
    }

    @Test
    @WithMockUser(username = "john_doe", roles = "USER")
    public void testListAccounts_AsUser_ShouldReturnUserAccounts() {
        runTest("List Accounts As User Should Return User Accounts", () -> {
            when(userService.findByUsername("john_doe")).thenReturn(Optional.of(testUser));
            when(accountService.findByUser(testUser)).thenReturn(List.of(testAccount));

            mockMvc.perform(get("/account/account_list"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("accounts"))
                    .andExpect(view().name("account/account_list"));
        });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testShowCreateAccountForm_ShouldReturnFormWithUsers() {
        runTest("Display Create Account Form Should Return Form With Users", () -> {
            when(userService.getAllUsers()).thenReturn(List.of(testUser));

            mockMvc.perform(get("/account/create"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("account"))
                    .andExpect(model().attributeExists("users"))
                    .andExpect(view().name("account/account_form"));
        });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testSearchAccounts_ShouldReturnSearchResults() {
        runTest("Search Accounts Should Return Search Results", () -> {
            when(accountService.searchAccounts("test")).thenReturn(List.of(testAccount));

            mockMvc.perform(get("/account/search").param("searchTerm", "test"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("accounts"))
                    .andExpect(view().name("account/account_list"));
        });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateAccount_AsAdmin_ShouldRedirect() {
        runTest("Create Account As Admin Should Redirect", () -> {
            mockMvc.perform(post("/account/create").with(csrf())
                            .param("accountName", "John's Account")
                            .param("accountType", "SAVINGS")
                            .param("balance", "5000")
                            .param("user.id", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/account/account_list"));
        });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testShowEditForm_ShouldReturnFormWithAccount() {
        runTest("Display Edit Form Should Return Form With Account", () -> {
            when(accountService.getAccountById(1L)).thenReturn(testAccount);

            mockMvc.perform(get("/account/edit/1"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("account"))
                    .andExpect(view().name("account/account_form"));
        });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateAccount_ShouldRedirect() {
        runTest("Update Account Should Redirect", () -> {
            mockMvc.perform(post("/account/update/1").with(csrf())
                            .param("id", "1")
                            .param("accountType", "SAVINGS")
                            .param("balance", "5000")
                            .param("user.id", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/account/account_list"));

            verify(accountService, times(1)).updateAccount(eq(1L), any(Account.class));
        });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteAccount_ShouldRedirect() {
        runTest("Delete Account Should Redirect", () -> {
            mockMvc.perform(get("/account/delete/1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/account/account_list"));

            verify(accountService, times(1)).deleteAccount(1L);
        });
    }

    @AfterAll
    public void flushReport() {
        extent.flush();
        System.out.println("✅ AccountControllerTest report generated.");
    }
    @FunctionalInterface
    interface ThrowingRunnable {
        void run() throws Exception;
    }


    private void runTest(String testName, ThrowingRunnable testLogic) {
    	test = ExtentReportManager.createTest(testName, ""
				+ "Controller Layer", "AccountController");
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
