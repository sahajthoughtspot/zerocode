package org.jsmart.zerocode.core.runner.parallel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeExecResultBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeReportBuilder;
import org.jsmart.zerocode.core.engine.listener.ZeroCodeTestReportJupiterListener;
import org.jsmart.zerocode.core.logbuilder.LogCorrelationshipPrinter;
import org.jsmart.zerocode.core.report.ZeroCodeReportGeneratorImpl;
import org.jsmart.zerocode.parallel.ExecutorServiceRunner;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.time.LocalDateTime.now;
import static org.jsmart.zerocode.core.domain.builders.ZeroCodeExecResultBuilder.newInstance;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.runner.Description.createTestDescription;

import java.util.List;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

public class LoadProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadProcessor.class);

    private final String loadPropertiesFile;
    private final AtomicInteger passedCounter = new AtomicInteger();
    private final AtomicInteger failedCounter = new AtomicInteger();
    private ExecutorServiceRunner executorServiceRunner;
    private boolean failed = true;
    private boolean passed = !failed;

    public LoadProcessor(String loadPropertiesFile) {
        this.loadPropertiesFile = loadPropertiesFile;
        executorServiceRunner = new ExecutorServiceRunner(loadPropertiesFile);
    }

    public LoadProcessor addTest(Class<?> testClass, String testMethod) {

        Runnable zeroCodeJunitTest = createRunnable(testClass, testMethod);

        executorServiceRunner.addRunnable(zeroCodeJunitTest);

        return this;
    }

    public LoadProcessor addJupiterTest(Class<?> testClass, String testMethod) {

        Runnable zeroCodeJunitTest = createJupiterRunnable(testClass, testMethod);

        executorServiceRunner.addRunnable(zeroCodeJunitTest);

        return this;
    }

    public boolean process() {
        executorServiceRunner.runRunnables();

        LOGGER.info(
                "\n------------------------------------"
                        + "\n   >> Total load test count:" + (failedCounter.get() + passedCounter.get())
                        + "\n   >> Passed count:" + passedCounter.get()
                        + "\n   >> Failed count:" + failedCounter.get()
                        + "\n------------------------------------");

        if (failedCounter.get() > 0) {
            return failed;
        }

        return passed;
    }

    private Runnable createJupiterRunnable(Class<?> testClass, String testMethod) {
        return () -> {

            LOGGER.info(Thread.currentThread().getName() + " Parallel Junit test- *Start. Time = " + now());

            final LauncherDiscoveryRequest request =
                    LauncherDiscoveryRequestBuilder.request()
                            .selectors(selectMethod(testClass, testMethod))
                            .build();
            final Launcher launcher = LauncherFactory.create();
            final SummaryGeneratingListener listener = new SummaryGeneratingListener();

            /// register listener
//            String testDescription = testClass + "#" + testMethod;
//            final String logPrefixRelationshipId = prepareRequestReport(testDescription);
//
//            ZeroCodeTestReportJupiterListener reportListener = new ZeroCodeTestReportJupiterListener(
//                    new ObjectMapper(),
//                    new ZeroCodeReportGeneratorImpl()
//            );
//
//            launcher.registerTestExecutionListeners(reportListener);
            /// register listener -done

            launcher.registerTestExecutionListeners(listener);
            launcher.execute(request);

            TestExecutionSummary summary = listener.getSummary();

            LOGGER.info(Thread.currentThread().getName() + " Parallel Junit test- *  End. Time = " + now());

            if (summary.getTotalFailureCount() > 0) {
                failedCounter.incrementAndGet();
                summary.getFailures().forEach(thisFailure -> {
                    TestIdentifier testIdentifier = thisFailure.getTestIdentifier();
                    String exceptionMessage = thisFailure.getException().getMessage();
                    LOGGER.info("\n----------------------------------------------------------------------\n");
                    LOGGER.info("\n###Test Failed Due To --> {}, \ntestIdentifier={}", exceptionMessage, testIdentifier);
                    LOGGER.info("\n----------------------------------------------------------------------\n");
                });
            } else {
                passedCounter.incrementAndGet();
            }

            //prepare response report
//            prepareResponseReport(logPrefixRelationshipId);
//            buildReportAndPrintToFile(testDescription);
        };
    }

    private Runnable createRunnable(Class<?> testClass, String testMathod) {
        return () -> {
            LOGGER.info(Thread.currentThread().getName() + " Parallel Junit test- *Start. Time = " + now());

            Result result = (new JUnitCore()).run(Request.method(testClass, testMathod));

            LOGGER.info(Thread.currentThread().getName() + " Parallel Junit test- *  End. Time = " + now());

            if (result.wasSuccessful()) {
                passedCounter.incrementAndGet();
            } else {
                failedCounter.incrementAndGet();
            }
        };
    }

    public boolean processMultiLoad() {
        executorServiceRunner.runRunnablesMulti();

        LOGGER.info(
                "\n------------------------------------"
                        + "\n   >> Total load test count:" + (failedCounter.get() + passedCounter.get())
                        + "\n   >> Passed count:" + passedCounter.get()
                        + "\n   >> Failed count:" + failedCounter.get()
                        + "\n------------------------------------");

        if (failedCounter.get() > 0) {
            return failed;
        }

        return passed;
    }

    // move to report class -new
    private LogCorrelationshipPrinter logCorrelationshipPrinter;
    private String prepareRequestReport(String description) {
        logCorrelationshipPrinter = LogCorrelationshipPrinter.newInstance(LOGGER);
        logCorrelationshipPrinter.stepLoop(0);
        final String logPrefixRelationshipId = logCorrelationshipPrinter.createRelationshipId();
        LocalDateTime timeNow = LocalDateTime.now();
        logCorrelationshipPrinter.aRequestBuilder()
                .stepLoop(0)
                .relationshipId(logPrefixRelationshipId)
                .requestTimeStamp(timeNow)
                .step(description);
        LOGGER.info("JUnit5 *requestTimeStamp:{}, \nJUnit Request:{}", timeNow, logPrefixRelationshipId);
        return logPrefixRelationshipId;
    }

    private void prepareResponseReport(String logPrefixRelationshipId) {
        LocalDateTime timeNow = LocalDateTime.now();
        LOGGER.info("JUnit5 *responseTimeStamp:{}, \nJUnit Response:{}", timeNow, logPrefixRelationshipId);
        logCorrelationshipPrinter.aResponseBuilder()
                .relationshipId(logPrefixRelationshipId)
                .responseTimeStamp(timeNow);

        logCorrelationshipPrinter.result(passed);
        logCorrelationshipPrinter.buildResponseDelay();
    }

    private void buildReportAndPrintToFile(String description) {
        ZeroCodeExecResultBuilder reportResultBuilder = newInstance().loop(0).scenarioName(description);
        reportResultBuilder.step(logCorrelationshipPrinter.buildReportSingleStep());

        ZeroCodeReportBuilder reportBuilder = ZeroCodeReportBuilder.newInstance().timeStamp(LocalDateTime.now());
        reportBuilder.result(reportResultBuilder.build());
        reportBuilder.printToFile(description + logCorrelationshipPrinter.getCorrelationId() + ".json");
    }

}
