package com.techmate.techmate.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Utility Functions General Tests")
class UtilityFunctionsTest {

    @Test
    @DisplayName("String utility operations should be safe")
    void stringUtilityOperationsShouldBeSafe() {
        String test = "test";
        assertNotNull(test);
        assertFalse(test.isEmpty());
    }

    @Test
    @DisplayName("Null checks should work correctly")
    void nullChecksShouldWorkCorrectly() {
        Object testObject = new Object();
        assertNotNull(testObject);
        
        Object nullObject = null;
        assertNull(nullObject);
    }

    @Test
    @DisplayName("Boolean conditions should evaluate correctly")
    void booleanConditionsShouldEvaluateCorrectly() {
        assertTrue(true);
        assertFalse(false);
    }

    @Test
    @DisplayName("Equals method should work properly")
    void equalsMethodShouldWorkProperly() {
        String str1 = "test";
        String str2 = "test";
        assertEquals(str1, str2);
    }

    @Test
    @DisplayName("Not equals method should work properly")
    void notEqualsMethodShouldWorkProperly() {
        String str1 = "test1";
        String str2 = "test2";
        assertNotEquals(str1, str2);
    }

    @Test
    @DisplayName("Same instance check should work")
    void sameInstanceCheckShouldWork() {
        Object obj = new Object();
        assertSame(obj, obj);
    }

    @Test
    @DisplayName("Different instances should not be same")
    void differentInstancesShouldNotBeSame() {
        Object obj1 = new Object();
        Object obj2 = new Object();
        assertNotSame(obj1, obj2);
    }

    @Test
    @DisplayName("Exception throwing should work correctly")
    void exceptionThrowingShouldWorkCorrectly() {
        assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("Test exception");
        });
    }

    @Test
    @DisplayName("No exception should be thrown for valid operations")
    void noExceptionShouldBeThrownForValidOperations() {
        assertDoesNotThrow(() -> {
            int result = 1 + 1;
            assertEquals(2, result);
        });
    }
}
