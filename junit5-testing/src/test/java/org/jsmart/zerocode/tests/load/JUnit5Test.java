package org.jsmart.zerocode.tests.load;

import org.jsmart.zerocode.tests.postgres.ExtentionA;
import org.jsmart.zerocode.tests.postgres.ExtentionB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({ExtentionA.class, ExtentionB.class})
public class JUnit5Test {

    @Test
    public void testX() {
        System.out.println("5 ###########################################testX()");
        assertTrue(2 == 3);
    }

    @Test
    public void testY() {
        System.out.println("5 ######################################-----testy()");
        assertTrue(2 == 2);
    }

}