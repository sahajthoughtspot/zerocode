package org.jsmart.zerocode.tests.load;


import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JUnit4Test {

    @Test
    public void testX() {
        System.out.println("4 ###########################################testX()");
        assertTrue(true);
    }

    @Test
    public void testY() {
        System.out.println("testY()");
    }

}