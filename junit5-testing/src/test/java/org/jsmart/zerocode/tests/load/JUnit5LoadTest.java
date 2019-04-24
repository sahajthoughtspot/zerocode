package org.jsmart.zerocode.tests.load;

import org.jsmart.zerocode.core.domain.LoadWith;
import org.jsmart.zerocode.core.domain.TestMapping;
import org.jsmart.zerocode.core.domain.TestMappings;
import org.jsmart.zerocode.tests.postgres.ExtentionLoad;
import org.jsmart.zerocode.tests.postgres.ExtentionLoadWip;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@LoadWith("load_generation.properties")
@ExtendWith({ExtentionLoadWip.class})
//@ExtendWith({ExtentionLoad.class})
public class JUnit5LoadTest {

    @Test
    @TestMappings({
            @TestMapping(testClass = JUnit5Test.class, testMethod = "testX"),
            @TestMapping(testClass = JUnit5Test.class, testMethod = "testY")
    })
    public void testLoad() {
    }

}