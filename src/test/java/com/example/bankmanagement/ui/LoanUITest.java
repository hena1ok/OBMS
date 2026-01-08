package com.example.bankmanagement.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import com.example.bankmanagement.utils.ExtentReportManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoanUITest {

	private WebDriver driver;
	private WebDriverWait wait;
	private static ExtentReports extent;
	private ExtentTest test;

	   protected void loginAsAdmin() {
	        driver.get("http://localhost:8080/login");
	        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
	        WebElement passwordField = driver.findElement(By.id("password"));
	        WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

	        usernameField.sendKeys("user");
	        passwordField.sendKeys("123");
	        loginButton.click();

	        wait.until(ExpectedConditions.urlContains("/dashboard"));
	    }
	@BeforeAll
	public void setupReport() {
		extent = ExtentReportManager.getInstance(); // shared instance
	}

	@BeforeEach
	public void setUp() {
		String browser = System.getProperty("browser", "chrome");

		switch (browser.toLowerCase()) {
		case "firefox":
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			break;
		case "edge":
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
			break;
		default:
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
		}

		driver.manage().window().maximize();
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		loginAsAdmin();
	}

	

	@Test
	public void testLoanFormSubmissionSuccess() {
		runTest("Loan Form Submission Success", () -> {
			
				driver.get("http://localhost:8080/loan/apply");

				WebElement amountField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("amount")));
				WebElement interestRateField = driver.findElement(By.id("interestRate"));
				WebElement termField = driver.findElement(By.id("term"));
				WebElement durationField = driver.findElement(By.id("duration"));
				Select loanTypeDropdown = new Select(driver.findElement(By.id("loanType")));
				WebElement loanPurposeField = driver.findElement(By.id("loanPurpose"));

				
				
				amountField.sendKeys("5000");
				interestRateField.sendKeys("5.5");
				termField.sendKeys("24");
				durationField.sendKeys("24");
				
				loanPurposeField.sendKeys("Home Renovation");

				for (WebElement option : loanTypeDropdown.getOptions()) {
				    if (option.isEnabled() && !option.getAttribute("value").isEmpty()) {
				    	loanTypeDropdown.selectByVisibleText(option.getText());
				        break;
				    }
				}
				WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
				scrollToAndClick(submitButton);

				WebElement successMessage = wait
						.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success")));
				assertNotNull(successMessage);
				assertTrue(successMessage.getText().toLowerCase().contains("loan")
						|| successMessage.getText().toLowerCase().contains("success"));

				test.pass("Loan submitted successfully and success message displayed.");
		
		});

	}

	@Test
	@Order(3)
	@DisplayName("Test Loan Form Validation Errors")
	public void testLoanFormValidationErrors() {
		runTest("Loan Form Validation Errors", () -> {
		
				driver.get("http://localhost:8080/loan/apply");

				WebElement submitButton = wait.until(
						ExpectedConditions.presenceOfElementLocated(By.cssSelector("button[type='submit']")));

				// Scroll into view and click to avoid ElementClickInterceptedException
				((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitButton);
				wait.until(ExpectedConditions.elementToBeClickable(submitButton));
				submitButton.click();

				WebElement validationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
					    By.cssSelector(".text-danger, .alert-danger, .invalid-feedback, .form-error")));

				assertNotNull(validationMessage);
				test.pass("✅ Validation errors appeared as expected when submitting an empty loan form.");
			
		});
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

	
	@AfterEach
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

	@AfterAll
	public void flushReport() {
		ExtentReportManager.flushReports();
		System.out.println("✅ LoanUI Test report generated.");

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
				+ "End To End", "Loan UI");
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
