package org.jsmart.zerocode.tests.postgres;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.jsmart.zerocode.core.domain.JsonTestCase;
import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.runner.parallel.LoadProcessor;
import org.jsmart.zerocode.tests.load.JUnit5Test;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

public class ExtentionLoad implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        System.out.println("ExtensionLoad - afterEach");
        Method testMethod = extensionContext.getRequiredTestMethod();
        Class<?> testClass = extensionContext.getRequiredTestClass();
        System.out.println("--- getRequiredTestClass: " + testClass);

        ///
        LoadWith loadWithProperties = testClass.getAnnotation(LoadWith.class);
        System.out.println("--- load properties: " + loadWithProperties.value());
        //LoadProcessor loadProcessor = new LoadProcessor(loadWithProperties.value());

        TestMapping[] testMappingArray = testMethod.getAnnotationsByType(TestMapping.class);

        final LauncherDiscoveryRequestBuilder requestBuilder = LauncherDiscoveryRequestBuilder.request();
        Arrays.stream(testMappingArray).forEach(thisMapping -> {

            System.out.println("----> thisMapping: " + thisMapping);
            requestBuilder.selectors(selectMethod(thisMapping.testClass(), thisMapping.testMethod()));
            //loadProcessor.addTest(thisChild.testClass(), thisChild.testMethod());
        });
        LauncherDiscoveryRequest request = requestBuilder.build();

        final Launcher launcher = LauncherFactory.create();

        final SummaryGeneratingListener listener = new SummaryGeneratingListener();


        launcher.registerTestExecutionListeners(listener);
        TestPlan testPlan = launcher.discover(request);

        // System.out.println("testPlan.getRoots: "
        // + ((InternalTestPlan) testPlan).getDelegate().children.get("[engine:junit-jupiter]/[class:org.jsmart.zerocode.tests.load.JUnit5Test]"));

        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        long testFoundCount = summary.getTestsFoundCount();
        List<TestExecutionSummary.Failure> failures = summary.getFailures();

        System.out.println("##### testFoundCount: " + testFoundCount);
        System.out.println("##### getTestsSucceededCount: " + summary.getTestsSucceededCount());
        System.out.println("##### getTestsFailedCount: " + summary.getTestsFailedCount());
        System.out.println("##### getTotalFailureCount: " + summary.getTotalFailureCount());
        System.out.println("##### failures size: " + failures.size());
//        if(failures.size() > 0){
//            System.out.println("##### failures getException: " + failures.get(0).getException());
//            System.out.println("##### failures toString: " + failures.get(0).toString());
//            fail("test failed due to so and so reason - Total failures:" + failures.size());
//        }

        System.out.println("_--------------------------------------------------------------------------------_");
        // 2. execute another
        final LauncherDiscoveryRequestBuilder requestBuilder2 = LauncherDiscoveryRequestBuilder.request();
        Arrays.stream(testMappingArray).forEach(thisMapping -> {

            System.out.println("----> thisMapping: " + thisMapping);
            requestBuilder2.selectors(selectMethod(thisMapping.testClass(), thisMapping.testMethod()));
            //loadProcessor.addTest(thisChild.testClass(), thisChild.testMethod());
        });
        LauncherDiscoveryRequest request2 = requestBuilder2.build();

        final Launcher launcher2 = LauncherFactory.create();

        final SummaryGeneratingListener listener2 = new SummaryGeneratingListener();


        launcher2.registerTestExecutionListeners(listener2);

        // System.out.println("testPlan.getRoots: "
        // + ((InternalTestPlan) testPlan).getDelegate().children.get("[engine:junit-jupiter]/[class:org.jsmart.zerocode.tests.load.JUnit5Test]"));

        launcher2.execute(request2);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
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
}
