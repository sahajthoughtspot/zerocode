package org.jsmart.zerocode.tests.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.jsmart.zerocode.core.di.provider.ObjectMapperProvider;
import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.report.ZeroCodeReportGenerator;
import org.jsmart.zerocode.core.report.ZeroCodeReportGeneratorImpl;
import org.jsmart.zerocode.core.runner.parallel.LoadProcessor;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.fail;

public class ExtentionLoadWip implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtentionLoadWip.class);
    private final ObjectMapper mapper = new ObjectMapperProvider().get();
    private final ZeroCodeReportGenerator reportGenerator = new ZeroCodeReportGeneratorImpl(mapper);

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        Method testMethod = extensionContext.getRequiredTestMethod();
        Class<?> testClass = extensionContext.getRequiredTestClass();
        String loadPropertiesFile = validateAndGetLoadPropertiesFile(testClass);
        LoadProcessor loadProcessor = new LoadProcessor(loadPropertiesFile);

        //-------------------------------------------
        // On/Off extent report
        // Load the key 'chart.dashboard.generation'
        // from 'loadPropertiesFile'
        //-------------------------------------------
        boolean chartAndDashBoardGenration = false;


        TestMapping[] testMappingArray = testMethod.getAnnotationsByType(TestMapping.class);

        Arrays.stream(testMappingArray).forEach(thisMapping -> {
            loadProcessor.addJupiterTest(thisMapping.testClass(), thisMapping.testMethod());
        });

        boolean hasFailed = loadProcessor.processMultiLoad();

        reportGenerator.generateCsvReport();
        if(chartAndDashBoardGenration){
            reportGenerator.generateExtentReport();
        }

        if (hasFailed) {
            String failureMessage = testClass.getName() + " with load/stress test(s): " + testMethod + " have Failed";
            LOGGER.error("\n" + failureMessage + ". \nSee the 'target/logs' for individual failures " +
                    "\n-Also- \nSee the 'target/' for granular 'csv report' for pass/fail/response-delay statistics.\uD83D\uDE0E");
            String testDescription = testClass + "#" + testMethod;
            fail(testDescription, new RuntimeException(failureMessage));
        } else {
            LOGGER.info("\nAll Passed \uD83D\uDC3C. \nSee the granular 'csv report' for individual test statistics.");
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
