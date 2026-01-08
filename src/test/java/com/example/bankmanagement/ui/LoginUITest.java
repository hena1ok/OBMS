package com.example.bankmanagement.ui;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import com.example.bankmanagement.utils.ExtentReportManager;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginUITest {

	private WebDriver driver;
	private WebDriverWait wait;
	private static ExtentReports extent;
	private ExtentTest test;

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
	}

	@Test
	public void testLoginSuccess() {
		runTest("Log in Success", () -> {
			try {

				driver.get("http://localhost:8080/login");

				WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
				WebElement passwordField = driver.findElement(By.id("password"));
				WebElement loginButton = driver.findElement(By.cssSelector("button[type='submit']"));

				usernameField.sendKeys("admin");
				passwordField.sendKeys("123");
				loginButton.click();

				wait.until(ExpectedConditions.urlToBe("http://localhost:8080/admin/dashboard"));
				assertEquals("http://localhost:8080/admin/dashboard", driver.getCurrentUrl());

				WebElement welcomeMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
				assertTrue(welcomeMessage.getText().contains("Welcome"));

				WebElement logoutButton = driver.findElement(By.cssSelector("a[href='/logout']"));
				assertNotNull(logoutButton);

				test.pass("Login with valid credentials successful.");
			} catch (Exception e) {
				test.fail("Login success test failed: " + e.getMessage());
				captureScreenshot(test, "testLoginSuccess");
				fail(e);
			}
		});

	}

	@Test
	public void testLoginFailure() {
		runTest("Login Failure", () -> {

			try {
				driver.get("http://localhost:8080/login");

				driver.findElement(By.id("username")).sendKeys("invaliduser");
				driver.findElement(By.id("password")).sendKeys("invalidpass");
				driver.findElement(By.cssSelector("button[type='submit']")).click();

				WebElement errorMessage = wait
						.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error-message")));
				assertNotNull(errorMessage);
				assertTrue(errorMessage.getText().contains("Invalid username or password"));

				test.pass("Error message displayed correctly on invalid login.");
			} catch (Exception e) {
				test.fail("Login failure test failed: " + e.getMessage());
				captureScreenshot(test, "testLoginFailure");
				fail(e);
			}
		});

	}

	private void captureScreenshot(ExtentTest test, String name) {
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
		System.out.println("✅ LoginUITest report generated.");

	}

	private void runTest(String testName, Runnable testLogic) {
		test = ExtentReportManager.createTest(testName, ""
				+ "End To End", "Login UI");
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
