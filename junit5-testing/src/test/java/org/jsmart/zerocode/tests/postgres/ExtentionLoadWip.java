package org.jsmart.zerocode.tests.postgres;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.runner.parallel.LoadProcessor;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;

public class ExtentionLoadWip implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtentionLoadWip.class);

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Method testMethod = extensionContext.getRequiredTestMethod();
        Class<?> testClass = extensionContext.getRequiredTestClass();
        String loadPropertiesFile = validateAndGetLoadPropertiesFile(testClass);
        String testDescription = testClass + "#" + testMethod;

        LoadProcessor loadProcessor = new LoadProcessor(loadPropertiesFile);

        TestMapping[] testMappingArray = testMethod.getAnnotationsByType(TestMapping.class);

        Arrays.stream(testMappingArray).forEach(thisMapping -> {
            loadProcessor.addJupiterTest(thisMapping.testClass(), thisMapping.testMethod());
        });

        boolean hasFailed = loadProcessor.processMultiLoad();
        if (hasFailed) {
            String failureMessage = testClass.getName() + " with load/stress test(s): " + testMethod + " have Failed";
            LOGGER.error(failureMessage + ". See target/logs -or- junit granular failure report(csv) -or- fuzzy search and filter report(html) for details");
            fail(testDescription, new RuntimeException(failureMessage));
        }

    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        System.out.println("ExtensionLoad - beforeEach");

    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("ExtensionLoad - afterAll");

    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        System.out.println("ExtensionLoad - beforeAll");

    }

    private String validateAndGetLoadPropertiesFile(Class<?> testClass) {
        LoadWith loadWithAnno = testClass.getAnnotation(LoadWith.class);
        if (loadWithAnno == null) {
            throw new RuntimeException("Ah! You missed to put the @LoadWith(...) on the load-generating test class >> "
                    + testClass.getName());
        }

        return loadWithAnno.value();
    }
}
