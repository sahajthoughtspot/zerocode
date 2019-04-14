package org.jsmart.zerocode.tests.postgres;

import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ExtentionA implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        System.out.println("ExtentionA - afterEach");

        Optional<Method> testMethod = extensionContext.getTestMethod();
        Method requiredTestMethod = extensionContext.getRequiredTestMethod();
        System.out.println("---");

    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        System.out.println("ExtentionA - beforeEach");

    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("ExtentionA - afterAll");

    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("ExtentionA - beforeAll");

    }
}
