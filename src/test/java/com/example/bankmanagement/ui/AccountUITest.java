package com.example.bankmanagement.ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.*;

import static org.junit.jupiter.api.Assertions.*;
import com.example.bankmanagement.utils.ExtentReportManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountUITest {

	private WebDriver driver;
	private WebDriverWait wait;

	private ExtentReports extent;
	private ExtentTest test;

	protected void loginAsAdmin() {
		driver.get("http://localhost:8080/login");
		WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
		WebElement passwordField = driver.findElement(By.id("password"));
		WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

		usernameField.sendKeys("admin");
		passwordField.sendKeys("123");
		loginButton.click();

		wait.until(ExpectedConditions.urlContains("/dashboard"));
	}

	@BeforeAll
	public void setupReport() {
		extent = ExtentReportManager.getInstance(); // shared instance
	}

	@BeforeEach
	public void setup() {
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		driver.manage().window().maximize();
		loginAsAdmin();
	}

	@Test
	public void testAccountCreation() {
	    runTest("Account Creation", () -> {
	        driver.get("http://localhost:8080/account/create");
	        wait.until(ExpectedConditions.titleContains("Create Account"));

	        // Handle the user email dropdown
	        if (driver.findElements(By.id("userEmailInput")).size() > 0) {
	            String testEmail = "user@gmail.com"; // Ensure this user exists

	            WebElement userEmailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userEmailInput")));
	            userEmailInput.sendKeys(testEmail);

	            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("userEmailDropdown")));
	            WebElement userEmailDropdown = driver.findElement(By.id("userEmailDropdown"));
	            Select userEmailSelect = new Select(userEmailDropdown);

	            try {
	                userEmailSelect.selectByVisibleText(testEmail);
	            } catch (NoSuchElementException e) {
	                if (userEmailSelect.getOptions().size() > 1) {
	                    userEmailSelect.selectByIndex(1);
	                } else {
	                    throw new AssertionError("No user options available for account creation.");
	                }
	            }
	        }

	        // Fill in the form
	        WebElement accountName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountName")));
	        WebElement accountType = driver.findElement(By.id("accountType"));
	        WebElement balance = driver.findElement(By.id("balance"));
	        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

	        // Create a truly unique name
	        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
	        String testAccountName = "TestAccount_" + uniqueId;

	        accountName.sendKeys(testAccountName);
	        new Select(accountType).selectByVisibleText("Savings");
	        balance.sendKeys("1000.00");

	        scrollToAndClick(submitButton);

	        // Validate successful creation
	        wait.until(ExpectedConditions.urlContains("/account/account_list"));
	        boolean isPresent = driver.getPageSource().contains(testAccountName);
	        assertTrue(isPresent, "New account was not listed after creation.");

	        test.info("✅ Successfully created and verified account: " + testAccountName);
	    });
	}

	@Test
	public void testAccountCreationWithInvalidBalance() {
		runTest("Account Creation With Invalid Balance", () -> {
			driver.get("http://localhost:8080/account/create");
			wait.until(ExpectedConditions.titleContains("Create Account"));

			WebElement accountNumber = driver.findElement(By.id("accountNumber"));
			WebElement accountName = driver.findElement(By.id("accountName"));
			WebElement accountType = driver.findElement(By.id("accountType"));
			WebElement balance = driver.findElement(By.id("balance"));
			WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));

			String uniqueAccountNumber = "ACC" + System.currentTimeMillis();
			String testAccountName = "Invalid Balance Account";

			accountNumber.sendKeys(uniqueAccountNumber);
			accountName.sendKeys(testAccountName);
			new Select(accountType).selectByVisibleText("Checking");
			balance.sendKeys("-500.00");

			scrollToAndClick(submitButton);

			// Wait for error message
			WebElement errorMessage = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error-message") // Replace
																											// with your
																											// actual
																											// error
																											// message
																											// selector
			));

			System.out.println("Error Message Displayed: " + errorMessage.getText());

			assertTrue(errorMessage.getText().contains("Balance must be positive"),
					"Expected error message not found.");
		});
	}

	@Test
	public void testUpdateAccount() {
		runTest("Update Account", () -> {
			driver.get("http://localhost:8080/account/account_list");

			// Find the first Edit button and click it
			WebElement editButton = wait
					.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn-warning.btn-sm")));
			editButton.click();

			wait.until(ExpectedConditions.titleContains("Update Account"));

			// Modify the account name
			WebElement accountName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountName")));
			accountName.clear();
			accountName.sendKeys("Updated Account Name");

			// Submit the form
			WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
			scrollToAndClick(submitButton);

			// Check for success message
			WebElement successMessage = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-success")));
			Assertions.assertTrue(successMessage.getText().toLowerCase().contains("successfully"));
		
		});
	}

	@Test
	public void testDeleteAccount() {
		runTest("Delete Account", () -> {
			driver.get("http://localhost:8080/account/account_list");

			// Find the first Delete button and click it
			WebElement deleteButton = wait
					.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.btn-danger.btn-sm")));
			deleteButton.click();

			// Handle the confirmation popup
			driver.switchTo().alert().accept(); // click "OK"

			// Wait for the success message after deletion
			WebElement successMessage = wait
					.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-success")));
			Assertions.assertTrue(successMessage.getText().toLowerCase().contains("deleted"));
			

		});

	}

	@AfterEach
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
		ExtentReportManager.flushReports();
	}

	@AfterAll
	public void finish() {
		System.out.println("✅ All AccountUI Tests completed.");
	}

	protected void captureScreenshot(ExtentTest test, String name) {
		try {
			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String path = "target/screenshots/" + name + "_" + System.currentTimeMillis() + ".png";
			File dest = new File(path);
			dest.getParentFile().mkdirs();
			Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
			test.addScreenCaptureFromPath(path);
		} catch (IOException e) {
			test.warning("Could not attach screenshot: " + e.getMessage());
		}
	}

	protected void scrollToAndClick(WebElement element) {
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);

		// Wait for element to be visible and clickable
		wait.until(ExpectedConditions.elementToBeClickable(element));

		try {
			element.click();
		} catch (ElementClickInterceptedException e) {
			// Try clicking via JavaScript as a fallback
			js.executeScript("arguments[0].click();", element);
		}
	}

	protected void runTest(String testName, Runnable testLogic) {
		test = ExtentReportManager.createTest(testName, ""
				+ "End To End", "Account UI");
	       
	    try {
	        testLogic.run();
	        test.pass("✅ Test passed successfully");
	    } catch (AssertionError e) {
	        captureScreenshot(test, testName);
	        test.fail("❌ Assertion failed: " + e.getMessage());
	        throw e;
	    } catch (Exception e) {
	        captureScreenshot(test, testName);
	        test.fail("❗ Unexpected exception: " + e.getMessage());
	        throw new RuntimeException(e);
	    }
	}


}
