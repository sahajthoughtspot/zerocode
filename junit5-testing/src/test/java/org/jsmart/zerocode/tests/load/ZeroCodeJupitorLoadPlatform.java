package org.jsmart.zerocode.tests.load;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeExecResultBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeReportBuilder;
import org.jsmart.zerocode.core.logbuilder.LogCorrelationshipPrinter;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jsmart.zerocode.core.domain.builders.ZeroCodeExecResultBuilder.newInstance;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.runner.Description.createTestDescription;

public class ZeroCodeJupitorLoadPlatform extends JUnitPlatform {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZeroCodeJupitorLoadPlatform.class);

    private final Class<?> testClass;
    private Description testDescription;
    protected boolean passed;

    public ZeroCodeJupitorLoadPlatform(Class<?> testClass) {
        super(testClass);
        this.testClass = testClass;
    }

    @Override
    public Description getDescription() {
        testDescription = createTestDescription(testClass, "testY");
        return testDescription;
    }

    @Override
    public void run(RunNotifier notifier) {
//        ZeroCodeTestReportJupiterListener reportListener = new ZeroCodeTestReportJupiterListener(
//                new ObjectMapper(),
//                new ZeroCodeReportGeneratorImpl()
//        );

//        notifier.addListener(reportListener);

        notifier.fireTestStarted(testDescription);
        final String logPrefixRelationshipId = prepareRequestReport(testDescription);

        ///
        final LauncherDiscoveryRequest request =
                LauncherDiscoveryRequestBuilder.request()
                        //.selectors(selectClass(JUnit5Test.class))
                        //.selectors(selectMethod(JUnit5Test.class, "testX"),selectMethod(JUnit5Test.class, "testY"))
                        .selectors(selectMethod(JUnit5Test.class, "testY"))
                        //.selectors(selectMethod(testMappings.get(0).testClass(), testMappings.get(0).testMethod()))
                        .build();

//        final LauncherDiscoveryRequestBuilder requestBuilder = LauncherDiscoveryRequestBuilder.request();
//        testMappings.forEach(thisTest -> {
//            requestBuilder.selectors(selectMethod(thisTest.getClass(), thisTest.testMethod()));
//        });
//        LauncherDiscoveryRequest request = requestBuilder.build();

        ///

        final Launcher launcher = LauncherFactory.create();

        final SummaryGeneratingListener listener = new SummaryGeneratingListener();


        launcher.registerTestExecutionListeners(listener);
        //launcher.registerTestExecutionListeners(listener, reportListener);
        TestPlan testPlan = launcher.discover(request);

        //listener.executionFinished(testPlan.getTestIdentifier());


        ///
        launcher.execute(request);
        //launcher.execute(testPlan, new TestExecutionListener[]{listener}); //also works fine

        ///

        /// report

        prepareResponseReport(logPrefixRelationshipId);

        buildReportAndPrintToFile(testDescription);

        notifier.fireTestFinished(testDescription);

        ///

        TestExecutionSummary summary = listener.getSummary();
        long testFoundCount = summary.getTestsFoundCount();
        List<TestExecutionSummary.Failure> failures = summary.getFailures();

        System.out.println("##### testFoundCount: " + testFoundCount);
        System.out.println("##### getTestsSucceededCount: " + summary.getTestsSucceededCount());
        System.out.println("##### getTestsFailedCount: " + summary.getTestsFailedCount());
        System.out.println("##### getTotalFailureCount: " + summary.getTotalFailureCount());
        System.out.println("##### failures size: " + failures.size());
        if(failures.size() > 0){
            passed = false;
            System.out.println("##### failures getException: " + failures.get(0).getException());
            System.out.println("##### failures toString: " + failures.get(0).toString());
        }
    }

    private LogCorrelationshipPrinter logCorrelationshipPrinter;

    private String prepareRequestReport(Description description) {
        logCorrelationshipPrinter = LogCorrelationshipPrinter.newInstance(LOGGER);
        logCorrelationshipPrinter.stepLoop(0);
        final String logPrefixRelationshipId = logCorrelationshipPrinter.createRelationshipId();
        LocalDateTime timeNow = LocalDateTime.now();
        logCorrelationshipPrinter.aRequestBuilder()
                .stepLoop(0)
                .relationshipId(logPrefixRelationshipId)
                .requestTimeStamp(timeNow)
                .step(description.getMethodName());
        LOGGER.info("JUnit *requestTimeStamp:{}, \nJUnit Request:{}", timeNow, logPrefixRelationshipId);
        return logPrefixRelationshipId;
    }

    private void buildReportAndPrintToFile(Description description) {
        ZeroCodeExecResultBuilder reportResultBuilder = newInstance().loop(0).scenarioName(description.getClassName());
        reportResultBuilder.step(logCorrelationshipPrinter.buildReportSingleStep());

        ZeroCodeReportBuilder reportBuilder = ZeroCodeReportBuilder.newInstance().timeStamp(LocalDateTime.now());
        reportBuilder.result(reportResultBuilder.build());
        reportBuilder.printToFile(description.getClassName() + logCorrelationshipPrinter.getCorrelationId() + ".json");
    }

    private void prepareResponseReport(String logPrefixRelationshipId) {
        LocalDateTime timeNow = LocalDateTime.now();
        LOGGER.info("JUnit *responseTimeStamp:{}, \nJUnit Response:{}", timeNow, logPrefixRelationshipId);
        logCorrelationshipPrinter.aResponseBuilder()
                .relationshipId(logPrefixRelationshipId)
                .responseTimeStamp(timeNow);

        logCorrelationshipPrinter.result(passed);
        logCorrelationshipPrinter.buildResponseDelay();
    }
}
