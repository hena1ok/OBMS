package com.example.bankmanagement.repository;

import com.example.bankmanagement.model.User;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import com.example.bankmanagement.utils.ExtentReportManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private ExtentReports extent;
    private ExtentTest test;

   @BeforeAll
    public void setupReport() {
        extent = ExtentReportManager.getInstance(); // shared instance
    }

   @BeforeEach
   public void setUp() {
       testUser = new User("testuser", "password123", "testuser@example.com", "John", "Doe",
                         "1234567890", "123 Main St", LocalDate.of(1990, 1, 1), "SAVINGS");
       testUser.setRegistrationDate(LocalDateTime.now());
   }

    @Test
    public void testSaveUser() {
    	runTest("Create Account", () -> {
    	User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser.getId(), "User ID should not be null after saving.");
        assertEquals(testUser.getUsername(), savedUser.getUsername(), "Usernames should match.");
        assertEquals(testUser.getEmail(), savedUser.getEmail(), "Emails should match.");
    	});
}

    @Test
    public void testFindByUsername() {
    	runTest("Create Account", () -> {
    	userRepository.save(testUser); // Save the test user first

        Optional<User> foundUser = userRepository.findByUsername("testuser");

        assertTrue(foundUser.isPresent(), "User should be found by username.");
        assertEquals("testuser", foundUser.get().getUsername(), "Username should match.");
    	});
  }

    @Test
    public void testFindByEmail() {
    	runTest("Create Account", () -> {
    	userRepository.save(testUser); // Save the test user first

        Optional<User> foundUser = userRepository.findByEmail("testuser@example.com");

        assertTrue(foundUser.isPresent(), "User should be found by email.");
        assertEquals("testuser@example.com", foundUser.get().getEmail(), "Email should match.");
    	});
}

    @Test
    public void testUserNotFoundByInvalidUsername() {
    	runTest("Create Account", () -> {
    	Optional<User> foundUser = userRepository.findByUsername("nonexistent");

        assertFalse(foundUser.isPresent(), "User should not be found by a non-existent username.");
    	});
}

    @Test
    public void testDeleteUser() {
    	runTest("Create Account", () -> {
    	User savedUser = userRepository.save(testUser);

        userRepository.delete(savedUser);
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        assertFalse(foundUser.isPresent(), "User should be deleted.");
    	});
 }

    @Test
    public void testUpdateUser() {
    	runTest("Create Account", () -> {
    	User savedUser = userRepository.save(testUser);
        savedUser.setFirstName("Jane");
        savedUser.setLastName("Smith");

        User updatedUser = userRepository.save(savedUser);

        assertEquals("Jane", updatedUser.getFirstName(), "First name should be updated.");
        assertEquals("Smith", updatedUser.getLastName(), "Last name should be updated.");
    	});
 }
    
    @AfterAll
	public void flushReport() {
    	ExtentReportManager.flushReports();
		System.out.println("✅ AccountControllerTest report generated.");

	}



private void runTest(String testName, Runnable testLogic) {
	test = ExtentReportManager.createTest(testName, ""
			+ "Repository Layer", "UserRepository");
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
