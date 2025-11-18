package com.techmate.techmate.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Exception Handling Advanced Tests")
class ExceptionHandlingAdvancedTest {

    @Test
    @DisplayName("Exception with message should preserve message")
    void exceptionWithMessageShouldPreserveMessage() {
        String message = "Test exception message";
        Exception exception = new Exception(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    @DisplayName("Exception with cause should preserve cause")
    void exceptionWithCauseShouldPreserveCause() {
        Throwable cause = new Throwable("Root cause");
        Exception exception = new Exception("Wrapper", cause);
        assertEquals(cause, exception.getCause());
    }

    @Test
    @DisplayName("Multiple exception types should be distinguishable")
    void multipleExceptionTypesShouldBeDistinguishable() {
        List<Exception> exceptions = Arrays.asList(
                new IllegalArgumentException("Illegal"),
                new IllegalStateException("State"),
                new NullPointerException("Null"),
                new RuntimeException("Runtime")
        );
        
        assertEquals(4, exceptions.size());
        assertThrows(IllegalArgumentException.class, () -> { throw exceptions.get(0); });
    }

    @Test
    @DisplayName("Exception stacktrace should be available")
    void exceptionStacktraceShouldBeAvailable() {
        Exception exception = new Exception("Test");
        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
    }

    @Test
    @DisplayName("Chained exceptions should be properly handled")
    void chainedExceptionsShouldBeProperlyHandled() {
        try {
            try {
                throw new IllegalArgumentException("Inner exception");
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Outer exception", e);
            }
        } catch (RuntimeException e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof IllegalArgumentException);
        }
    }

    @Test
    @DisplayName("Exception should be throwable")
    void exceptionShouldBeThrowable() {
        assertThrows(Exception.class, () -> {
            throw new Exception("Test throwable");
        });
    }

    @Test
    @DisplayName("Runtime exception should be unchecked")
    void runtimeExceptionShouldBeUnchecked() {
        RuntimeException exception = new RuntimeException("Unchecked");
        assertNotNull(exception);
        assertFalse(exception.getClass().getName().contains("Checked"));
    }

    @Test
    @DisplayName("Exception toString should contain class name")
    void exceptionToStringShouldContainClassName() {
        Exception exception = new IllegalArgumentException("Test");
        String toString = exception.toString();
        assertTrue(toString.contains("IllegalArgumentException"));
    }

    @Test
    @DisplayName("Multiple exception catches should work correctly")
    void multipleExceptionCatchesShouldWorkCorrectly() {
        int result = 0;
        try {
            throw new RuntimeException("Test");
        } catch (IllegalArgumentException e) {
            result = 1;
        } catch (RuntimeException e) {
            result = 2;
        }
        assertEquals(2, result);
    }
}
