package com.example.bankmanagement.controller;

import com.example.bankmanagement.controller.AccountControllerTest.ThrowingRunnable;
import com.example.bankmanagement.model.Role;
import com.example.bankmanagement.model.Support;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.RoleRepository;
import com.example.bankmanagement.security.CustomUserDetails;
import com.example.bankmanagement.service.RoleService;
import com.example.bankmanagement.service.SupportService;
import com.example.bankmanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.example.bankmanagement.utils.ExtentReportManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

	@Mock
	private UserService userService;

	@Mock
	private RoleService roleService;

	@Mock
	private RoleRepository roleRepository;

	@Mock
	private SupportService supportService;

	@Mock
	private Model model;

	@Mock
	private RedirectAttributes redirectAttributes;

	@InjectMocks
	private UserController userController;

	private MockMvc mockMvc;
	private User testUser;
	private Role testRole;

	private ExtentReports extent;
	private ExtentTest test;

	@BeforeAll
	public void setupReport() {
		extent = ExtentReportManager.getInstance(); // shared instance
	}

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

		testUser = new User();
		testUser.setId(1L);
		testUser.setUsername("testuser");
		testUser.setEmail("test@example.com");
		testUser.setFirstName("Test");
		testUser.setLastName("User");
		testUser.setPhone("1234567890");
		testUser.setAccountType("SAVINGS");
		testUser.setAddress("123 Main St");
		testUser.setDob(LocalDate.of(1990, 1, 1));
		testUser.setRegistrationDate(LocalDateTime.now());
		testUser.setEnabled(true);

		testRole = new Role();
		testRole.setId(1L);
		testRole.setName("ROLE_USER");
	}

	@Test
	@WithMockUser
	public void testUpdateProfile() throws Exception {
		runTest("Create Account", () -> {
			Authentication authentication = mock(Authentication.class);
			when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(testUser));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			doNothing().when(userService).updateUser(anyLong(), any(User.class));

			mockMvc.perform(post("/user/manage_profile").flashAttr("user", testUser))
					.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/manage_profile"))
					.andExpect(flash().attributeExists("successMessage"));

		});
	}

	@Test
	public void testSearchUsers() throws Exception {
		runTest("Create Account", () -> {
			when(userService.searchUsers("test")).thenReturn(Collections.singletonList(testUser));

			mockMvc.perform(get("/user/search").param("query", "test")).andExpect(status().isOk())
					.andExpect(view().name("user/manage_user")).andExpect(model().attributeExists("users"));

		});
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void testUpdateUser() throws Exception {
		runTest("Create Account", () -> {
			doNothing().when(userService).updateUser(anyLong(), any(User.class));

			mockMvc.perform(post("/user/manage_profile/1").flashAttr("user", testUser))
					.andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/user/manage_users"));

		});
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void testDeleteUser() throws Exception {
		runTest("Create Account", () -> {
			doNothing().when(userService).deleteUser(1L);

			mockMvc.perform(get("/user/delete/1")).andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/user/manage_users"));

		});
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void testToggleUser() throws Exception {
		runTest("Create Account", () -> {
			when(userService.getUserById(1L)).thenReturn(testUser);
			doNothing().when(userService).updateUser(anyLong(), any(User.class));

			mockMvc.perform(get("/user/toggle/1")).andExpect(status().is3xxRedirection())
					.andExpect(redirectedUrl("/user/manage_users"));

		});
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void testCreateUser_Failure() throws Exception {
		runTest("Create Account", () -> {
			when(roleRepository.findById(1L)).thenReturn(Optional.empty());

			mockMvc.perform(post("/user/create").param("username", "newuser").param("password", "password")
					.param("roleIds", "1")).andExpect(status().isBadRequest());

		});
	}

	private String asJsonString(Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	public void flushReport() {
		ExtentReportManager.flushReports();
		System.out.println("✅ UserControllerTest report generated.");

	}

	@FunctionalInterface
	interface ThrowingRunnable {
		void run() throws Exception;
	}

	private void runTest(String testName, ThrowingRunnable testLogic) {
		test = ExtentReportManager.createTest(testName, ""
				+ "Controller Layer", "UserController");
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