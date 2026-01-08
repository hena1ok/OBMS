package com.example.bankmanagement;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Bank Management System Complete Test Suite")
@SelectPackages({

		"com.example.bankmanagement.controller",
		"com.example.bankmanagement.repository",
		"com.example.bankmanagement.service",
		"com.example.bankmanagement.ui" })

public class AllTests {
	// This class remains empty, it's just a holder for the suite annotations
}