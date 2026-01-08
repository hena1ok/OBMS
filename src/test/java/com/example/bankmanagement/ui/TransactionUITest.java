package com.example.bankmanagement.ui;

import com.aventstack.extentreports.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import com.example.bankmanagement.utils.ExtentReportManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TransactionUITest {

	private WebDriver driver;
	private WebDriverWait wait;
	private static ExtentReports extent;
	private ExtentTest test;

	@BeforeAll
	public void setupReport() {
		extent = ExtentReportManager.getInstance(); // from your custom utility class
	}

	@BeforeEach
	public void setUp(TestInfo testInfo) {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		driver.manage().window().maximize();
		loginAsAdmin();
	}

	private void loginAsAdmin() {
		driver.get("http://localhost:8080/login");
		WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
		WebElement passwordField = driver.findElement(By.id("password"));
		WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

		usernameField.sendKeys("admin");
		passwordField.sendKeys("123");
		loginButton.click();

		wait.until(ExpectedConditions.urlContains("/dashboard"));
	}

	@Test
	@Order(1)
	@DisplayName("Test Creating a Transaction")
	public void testCreateTransaction() {
		runTest("Create Transaction", () -> {
			try {
				driver.get("http://localhost:8080/transactions/create");

				// Wait for the form to load
				WebElement accountDropdown = wait
						.until(ExpectedConditions.visibilityOfElementLocated(By.id("accountId")));
				new Select(accountDropdown).selectByIndex(1); // Select the second option (first real account)

				WebElement amountField = driver.findElement(By.id("amount"));
				amountField.sendKeys("1000");

				Select transactionTypeSelect = new Select(driver.findElement(By.id("transactionType")));
				transactionTypeSelect.selectByVisibleText("Deposit");

				WebElement description = driver.findElement(By.id("description"));
				description.sendKeys("UI test transaction");

				// Scroll and click the submit button using JavaScript
				WebElement submitButton = wait
						.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[type='submit']")));
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});",
						submitButton);
				((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitButton);

				// Wait for and assert success message
				WebElement successMessage = wait
						.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert-success")));
				assertTrue(successMessage.getText().toLowerCase().contains("successfully"));
				test.pass("✅ Transaction created successfully.");
			} catch (Exception e) {
				captureScreenshot("testCreateTransaction");
				test.fail("❌ Transaction creation failed: " + e.getMessage());
				fail(e);
			}
		});
	}

	@Test
	@Order(2)
	@DisplayName("Test Transaction Form Validation Errors")
	public void testTransactionFormValidationErrors() {
		runTest("Transaction Form Validation Errors", () -> {
			try {
				driver.get("http://localhost:8080/transactions/create");

				WebElement submitButton = wait
						.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[type='submit']")));

				// Scroll into view to avoid click interception
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);

				scrollToAndClick(submitButton);
				
				// Wait again after scroll to ensure it's clickable
				wait.until(ExpectedConditions.elementToBeClickable(submitButton));
				submitButton.click();

				WebElement errorMsg = wait.until(
						ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".text-danger, .alert-danger")));

				assertNotNull(errorMsg);
				test.pass("✅ Validation errors displayed as expected.");
			} catch (Exception e) {
				captureScreenshot("testTransactionFormValidationErrors");
				test.fail("❌ Validation error test failed: " + e.getMessage());
				fail(e);
			}
		});
	}

	private void captureScreenshot(String testName) {
		try {
			File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			String path = "target/screenshots/" + testName + "_" + System.currentTimeMillis() + ".png";
			File dest = new File(path);
			dest.getParentFile().mkdirs();
			Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
			test.addScreenCaptureFromPath(path);
		} catch (IOException e) {
			test.warning("⚠️ Failed to capture screenshot: " + e.getMessage());
		}
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
		System.out.println("✅ All TransactionUITests completed.");
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
	private void runTest(String testName, Runnable testLogic) {
		test = ExtentReportManager.createTest(testName, ""
				+ "End To End", "Transaction UI");
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
