package org.jsmart.zerocode.tests.load;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
//import org.jsmart.zerocode.tests.postgres.ZeroCodeLoadExtention;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.runner.RunWith;

@RunWith(ZeroCodeJupitorLoadPlatform.class)
@IncludeEngines("junit-jupiter")
//@RunWith(JUnitPlatform.class)
//@TestMapping(testClass = JUnit5Test.class, testMethod = "testX")
//@ExtendWith(ZeroCodeLoadExtention.class)
public class LoadJUnit5ExtendTest {

}