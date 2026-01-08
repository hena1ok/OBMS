package com.example.bankmanagement.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static final String REPORT_DIR = "target/reports/";
    private static final String RESOURCES_DIR = "src/test/resources/";
    private static final String LOGO_FILE = "logo.png";
    private static final String CSS_FILE = "extent-styles.css";
    private static final String JS_FILE = "extent-scripts.js";

    private static String reportBaseName;

    private ExtentReportManager() {
        throw new IllegalStateException("Utility class");
    }

    public static synchronized ExtentReports getInstance() {
        if (extent == null) {
            initializeExtentReports();
        }
        return extent;
    }

    public static String getReportBaseName() {
        return reportBaseName;
    }

    private static void initializeExtentReports() {
        try {
            System.out.println("Initializing ExtentReports...");
            createReportDirectory();
            String timestamp = getCurrentTimestamp();
            reportBaseName = "FullTestReport_" + timestamp;

            ExtentSparkReporter sparkReporter = createSparkReporter(reportBaseName);

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);

            configureSystemInfo();
        } catch (Exception e) {
            throw new ReportInitializationException("Failed to initialize ExtentReports", e);
        }
    }

    private static void createReportDirectory() throws IOException {
        Path reportPath = Paths.get(REPORT_DIR);
        if (!Files.exists(reportPath)) {
            Files.createDirectories(reportPath);
        }
        copyResourceFiles();
    }

    private static void copyResourceFiles() throws IOException {
        copyFileIfExists(LOGO_FILE);
        copyFileIfExists(CSS_FILE);
        copyFileIfExists(JS_FILE);
    }

    private static void copyFileIfExists(String fileName) throws IOException {
        Path source = Paths.get(RESOURCES_DIR, fileName);
        Path destination = Paths.get(REPORT_DIR, fileName);

        if (Files.exists(source)) {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied: " + fileName);
        } else {
            System.out.println("File not found: " + source.toAbsolutePath());
        }
    }

    private static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    private static ExtentSparkReporter createSparkReporter(String reportBaseName) throws IOException {
        String htmlReportPath = REPORT_DIR + reportBaseName + ".html";
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(htmlReportPath);

        sparkReporter.config().setReportName("🏦 OBMS | Full Functional Test Report");
        sparkReporter.config().setDocumentTitle("Online Bank Management System - QA Report");

        sparkReporter.config().setTheme(Theme.DARK);
        sparkReporter.config().setDocumentTitle("Bank Management System - Test Report");
        sparkReporter.config().setReportName("OBMS | Full Test Execution Report");
        sparkReporter.config().setEncoding("UTF-8");
        sparkReporter.config().setTimeStampFormat("EEEE, MMMM dd, yyyy HH:mm:ss a");

        try {
            Path cssPath = Paths.get(REPORT_DIR, CSS_FILE);
            if (Files.exists(cssPath)) {
                sparkReporter.config().setCSS(Files.readString(cssPath));
            }
            Path jsPath = Paths.get(REPORT_DIR, JS_FILE);
            if (Files.exists(jsPath)) {
                sparkReporter.config().setJS(Files.readString(jsPath));
            }
        } catch (IOException e) {
            System.err.println("⚠️ Warning loading custom assets: " + e.getMessage());
        }

        return sparkReporter;
    }

    private static void configureSystemInfo() {
        extent.setSystemInfo("Project", "Online Bank Management System (OBMS)");
        extent.setSystemInfo("Environment", System.getProperty("env", "QA"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Spring Boot Version", "3.3.2");
        extent.setSystemInfo("Test Lead", "Henok Yizelkal");
        extent.setSystemInfo("QA Engineers", "Kidus Tekle, Yedida Solomon, Amanuel Haile, Yusuf Temam, Eyosias Tewdros");
    }

    /**
     * Creates and returns a test with layer and class categories applied.
     * Call this method from your test classes.
     *
     * @param testName  the test method name or display name
     * @param layerName the layer under test (e.g., "Service", "Controller")
     * @param className the class under test (e.g., "UserService", "LoanController")
     * @return ExtentTest with categories assigned
     */
    public static ExtentTest createTest(String testName, String layerName, String className) {
        ExtentTest test = getInstance().createTest(testName);
        if (layerName != null && !layerName.isEmpty()) {
            test.assignCategory(layerName);
        }
        if (className != null && !className.isEmpty()) {
            test.assignCategory(className);
        }
        return test;
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
            System.out.println("✅ HTML report: " + getHtmlReportPath());
            try {
                generatePdfFromHtml();
                System.out.println("✅ PDF report: " + getPdfReportPath());
            } catch (Exception e) {
                System.err.println("❌ PDF generation failed: " + e.getMessage());
            }
        }
    }

    private static String getHtmlReportPath() {
        return REPORT_DIR + reportBaseName + ".html";
    }

    private static String getPdfReportPath() {
        return REPORT_DIR + reportBaseName + ".pdf";
    }

    private static void generatePdfFromHtml() throws Exception {
        File inputHtml = new File(getHtmlReportPath());
        if (!inputHtml.exists()) throw new FileNotFoundException("HTML report not found.");

        Document jsoupDoc = Jsoup.parse(inputHtml, "UTF-8");
        jsoupDoc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        jsoupDoc.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);

        org.w3c.dom.Document w3cDoc = new W3CDom().fromJsoup(jsoupDoc);

        try (OutputStream os = new FileOutputStream(getPdfReportPath())) {
            ITextRenderer renderer = new ITextRenderer();
            String baseUrl = new File(REPORT_DIR).toURI().toURL().toString();
            renderer.setDocument(w3cDoc, baseUrl);
            renderer.layout();
            renderer.createPDF(os);
        }
    }

    private static class ReportInitializationException extends RuntimeException {
        public ReportInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
