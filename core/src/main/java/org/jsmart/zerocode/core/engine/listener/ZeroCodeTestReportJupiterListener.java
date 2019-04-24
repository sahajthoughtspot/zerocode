package org.jsmart.zerocode.core.engine.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import java.time.LocalDateTime;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeExecResultBuilder;
import org.jsmart.zerocode.core.domain.builders.ZeroCodeReportBuilder;
import org.jsmart.zerocode.core.logbuilder.LogCorrelationshipPrinter;
import org.jsmart.zerocode.core.report.ZeroCodeReportGenerator;
import org.junit.platform.commons.util.PreconditionViolationException;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import static org.jsmart.zerocode.core.domain.builders.ZeroCodeExecResultBuilder.newInstance;
import static org.junit.platform.engine.TestExecutionResult.Status.FAILED;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Siddha on 24-jul-2016
 */
public class ZeroCodeTestReportJupiterListener implements TestExecutionListener {
    private static final org.slf4j.Logger LOGGER = getLogger(ZeroCodeTestReportJupiterListener.class);

    private final ObjectMapper mapper;

    private final ZeroCodeReportGenerator reportGenerator;

    private final Class<?> testClass;
    private final String testMethod;
    private String testDescription;

    private LogCorrelationshipPrinter logCorrelationshipPrinter;
    private String logPrefixRelationshipId;
    private boolean passed=true;

    @Inject
    public ZeroCodeTestReportJupiterListener(ObjectMapper mapper,
                                             ZeroCodeReportGenerator injectedReportGenerator,
                                             Class<?> testClass,
                                             String testMethod) {
        this.mapper = mapper;
        this.reportGenerator = injectedReportGenerator;
        this.testClass = testClass;
        this.testMethod = testMethod;
        this.testDescription = testClass + "#" + testMethod;

    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        System.out.println("-------------------> testPlan:start-time:" + LocalDateTime.now());
        logPrefixRelationshipId = prepareRequestReport(testDescription);
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        System.out.println("-------------------> testPlan:finish-time:" + LocalDateTime.now());
        //prepare response report
        prepareResponseReport(logPrefixRelationshipId);
        buildReportAndPrintToFile(testDescription);
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if(testExecutionResult.getStatus().equals(FAILED)){
            passed = false;
        }
    }

    //@Override
    public void testPlanExecutionFinished_WIP(TestPlan testPlan) {

    }

    private void generateChartsAndReports() {

        reportGenerator.generateCsvReport();

        /**
         * Not compatible with open source license i.e. why not activated But if it has to be used inside intranet,
         * then a single Developer's license should do. But visit www.highcharts.com for details.

         * https://shop.highsoft.com/faq
         * If I am using the Software on a commercial company´s intranet, does it require a license?
         Yes. The Developer License allows you to install and use the software on a commercial company's intranet.
         */
        //reportGenerator.generateHighChartReport();

        reportGenerator.generateExtentReport();
    }

    private String prepareRequestReport(String description) {
        logCorrelationshipPrinter = LogCorrelationshipPrinter.newInstance(LOGGER);
        logCorrelationshipPrinter.stepLoop(0);
        final String logPrefixRelationshipId = logCorrelationshipPrinter.createRelationshipId();
        LocalDateTime timeNow = LocalDateTime.now();
        logCorrelationshipPrinter.aRequestBuilder()
                .stepLoop(null)
                .relationshipId(logPrefixRelationshipId)
                .requestTimeStamp(timeNow)
                .step(testMethod);
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
        ZeroCodeExecResultBuilder reportResultBuilder = newInstance().loop(0).scenarioName(testClass.getName());
        reportResultBuilder.step(logCorrelationshipPrinter.buildReportSingleStep());

        ZeroCodeReportBuilder reportBuilder = ZeroCodeReportBuilder.newInstance().timeStamp(LocalDateTime.now());
        reportBuilder.result(reportResultBuilder.build());
        reportBuilder.printToFile(description + logCorrelationshipPrinter.getCorrelationId() + ".json");
    }


}