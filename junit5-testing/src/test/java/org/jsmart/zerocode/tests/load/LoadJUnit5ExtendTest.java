package org.jsmart.zerocode.tests.load;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
//import org.jsmart.zerocode.tests.postgres.ZeroCodeLoadExtention;
import org.jsmart.zerocode.core.domain.TestMappings;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.runner.RunWith;

@RunWith(ZeroCodeJupitorLoadPlatform.class)
@TestMappings({
        @TestMapping(testClass = JUnit5Test.class, testMethod = "testX"),
        //@TestMapping(testClass = JUnit5Test.class, testMethod = "testY")
})
public class LoadJUnit5ExtendTest {

}