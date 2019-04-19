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

        ExtensionContext extensionContext1 = extensionContext.getParent().get();
        System.out.println("--- getRoot : " + extensionContext1.getRoot());
        System.out.println("--- getParent: " + extensionContext1);
        //System.out.println("--- getRequiredTestMethod: " + extensionContext1.getRequiredTestMethod());
        System.out.println("--- getTestMethod: " + extensionContext1.getTestMethod());
        System.out.println("--- getRequiredTestClass: " + extensionContext1.getRequiredTestClass());
        System.out.println("--- getRequiredTestClass: " + extensionContext1.getTestInstance());
        System.out.println("--- extensionContext1.getUniqueId(): " + extensionContext1.getUniqueId());


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
