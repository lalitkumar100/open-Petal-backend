package com.crimsonlogic.open_petal_backend.util;

import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

/**
 * Global JUnit 5 Extension that logs the execution, method name, and business purpose 
 * (@DisplayName) of every test case to the system console.
 */
public class TestLoggerExtension implements BeforeTestExecutionCallback, TestWatcher {

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        String testClass = context.getRequiredTestClass().getSimpleName();
        String testMethod = context.getRequiredTestMethod().getName();
        String displayName = context.getDisplayName();

        System.out.println("================================================================================");
        System.out.println(">> [RUNNING TEST]: " + testClass + " # " + testMethod);
        System.out.println("   [PURPOSE / DESCRIPTION]: " + displayName);
        System.out.println("--------------------------------------------------------------------------------");
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        System.out.println("   [STATUS]: PASSED SUCCESSFUL\n");
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        System.out.println("   [STATUS]: FAILED - " + (cause != null ? cause.getMessage() : "Unknown error") + "\n");
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        System.out.println("   [STATUS]: SKIPPED - " + reason.orElse("No reason provided") + "\n");
    }
}
