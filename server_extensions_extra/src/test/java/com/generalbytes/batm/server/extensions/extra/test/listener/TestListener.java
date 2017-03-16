package com.generalbytes.batm.server.extensions.extra.test.listener;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

/**
 * @author ludx
 */
public class TestListener extends TestListenerAdapter {

    @Override
    public void onTestFailure(ITestResult testResult) {
        printTestResult(testResult);
    }

    @Override
    public void onTestSkipped(ITestResult testResult) {
        printTestResult(testResult);
    }

    @Override
    public void onTestSuccess(ITestResult testResult) {
        printTestResult(testResult);
    }

    @Override
    public void onStart(ITestContext testContext) {
        Reporter.log("\n===[ RUNNING TESTS ]============================================================================\n", true);
    }

    @Override
    public void onFinish(ITestContext testContext) {
        Reporter.log("\n===[ TESTS RUN ]================================================================================\n", true);
    }

    private void printTestResult(ITestResult testResult) {
        String status = getStatus(testResult.getStatus());
        Reporter.log(testResult.getTestClass().getName() + "." + testResult.getName() + "(): " + status, true);
    }

    private String getStatus(int status) {
        switch (status) {
            case ITestResult.SUCCESS:
                return "SUCCESS";
            case ITestResult.FAILURE:
                return "FAILURE";
            case ITestResult.SKIP:
                return "SKIP";
            default:
                return "PROBLEM";
        }
    }
}
