package org.jsmart.zerocode.tests.postgres;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({ExtentionA.class, ExtentionB.class})
public class SampleExtentionTest {

    @Test
    public void testX() {
        System.out.println("testX()");
        assertTrue(true);
    }

    @Test
    public void testY() {
        System.out.println("testY()");
    }

}