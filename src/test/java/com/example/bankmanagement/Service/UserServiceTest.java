package com.example.bankmanagement.Service;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.example.bankmanagement.model.Account;
import com.example.bankmanagement.model.Role;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.repository.AccountRepository;
import com.example.bankmanagement.repository.RoleRepository;
import com.example.bankmanagement.repository.UserRepository;
import com.example.bankmanagement.service.UserService;
import com.example.bankmanagement.utils.ExtentReportManager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private ExtentReports extent;
    private ExtentTest test;

    @BeforeAll
    void setupReport() {
    	
        extent = ExtentReportManager.getInstance();
    }
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, roleRepository, passwordEncoder, accountRepository);
        
    }

  

    @Test
    void testGetAllUsers() {
        runTest("Get All Users", () -> {
            List<User> mockUsers = Arrays.asList(new User(), new User());
            when(userRepository.findAll()).thenReturn(mockUsers);

            List<User> result = userService.getAllUsers();
            assertEquals(2, result.size());
            verify(userRepository).findAll();
        });
    }

    @Test
    void testGetUserById_Found() {
        runTest("Get User By ID Found", () -> {
            User mockUser = new User();
            when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

            User result = userService.getUserById(1L);
            assertEquals(mockUser, result);
        });
    }

    @Test
    void testSaveUserWithRoles() {
        runTest("Save User With Roles", () -> {
            User user = new User();
            user.setPassword("password");
            Set<String> roleNames = new HashSet<>(Collections.singleton("ROLE_USER"));

            Role role = new Role();
            role.setName("ROLE_USER");

            when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
            when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
            when(userRepository.save(user)).thenReturn(user);

            User result = userService.saveUser(user, roleNames);
            assertEquals("encodedPassword", result.getPassword());
            assertNotNull(result.getRegistrationDate());
        });
    }

    @Test
    void testUpdateUser() {
        runTest("Update User", () -> {
            User existingUser = new User();
            existingUser.setId(1L);
            existingUser.setFirstName("Old");

            User updatedUser = new User();
            updatedUser.setFirstName("New");

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.save(existingUser)).thenReturn(existingUser);

            userService.updateUser(1L, updatedUser);
            assertEquals("New", existingUser.getFirstName());
            verify(userRepository).save(existingUser);
        });
    }

    @Test
    void testDeleteUser() {
        runTest("Delete User", () -> {
            doNothing().when(userRepository).deleteById(1L);
            userService.deleteUser(1L);
            verify(userRepository).deleteById(1L);
        });
    }

    @Test
    void testExistsByUsername() {
        runTest("Exists By Username", () -> {
            when(userRepository.existsByUsername("testuser")).thenReturn(true);
            assertTrue(userService.existsByUsername("testuser"));
        });
    }

    @Test
    void testExistsByEmail() {
        runTest("Exists By Email", () -> {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
            assertTrue(userService.existsByEmail("test@example.com"));
        });
    }

    @Test
    void testGetCurrentUser_Authenticated() {
        runTest("Get Current User Authenticated", () -> {
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("testuser");

            Authentication authentication = mock(Authentication.class);
            when(authentication.getPrincipal()).thenReturn(userDetails);

            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);

            User mockUser = new User();
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

            Optional<User> result = userService.getCurrentUser();
            assertTrue(result.isPresent());
        });
    }

    @Test
    void testAssignRolesToUser() {
        runTest("Assign Roles To User", () -> {
            User user = new User();
            user.setId(1L);

            Role role = new Role();
            role.setName("ROLE_ADMIN");

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(role));
            when(userRepository.save(user)).thenReturn(user);

            Set<String> roles = new HashSet<>(Collections.singletonList("ROLE_ADMIN"));
            userService.assignRolesToUser(1L, roles);

            assertTrue(user.getRoles().contains(role));
        });
    }

    private void runTest(String testName, Runnable testLogic) {
    	test = ExtentReportManager.createTest(testName, "Service Layer", "UserService");
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
    void flushReport() {
    	ExtentReportManager.flushReports();
        System.out.println("✅ UserServiceTest report generated.");
    }
}
