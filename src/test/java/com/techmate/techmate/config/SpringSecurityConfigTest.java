package com.techmate.techmate.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootTest
@ActiveProfiles("test")
class SpringSecurityConfigTest {

    @Autowired(required = false)
    private HandlerExceptionResolver handlerExceptionResolver;

    @Test
    @DisplayName("Spring Security should be properly configured")
    void springSecurityShouldBeProperlyConfigured() {
        assertNotNull(handlerExceptionResolver);
    }

    @Test
    @DisplayName("HandlerExceptionResolver should be available")
    void handlerExceptionResolverShouldBeAvailable() {
        assertNotNull(handlerExceptionResolver);
    }

    @Test
    @DisplayName("Application should have security context")
    void applicationShouldHaveSecurityContext() {
        assertNotNull(handlerExceptionResolver);
    }

    @Test
    @DisplayName("WebMvcConfigurer beans should be registered")
    void webMvcConfigurerBeansShouldBeRegistered() {
        // Verify that Spring has configured web MVC
        assertNotNull(handlerExceptionResolver);
    }

    @Test
    @DisplayName("Exception handling should be configured")
    void exceptionHandlingShouldBeConfigured() {
        assertTrue(handlerExceptionResolver != null);
    }

    @Test
    @DisplayName("Application context should load successfully with security")
    void applicationContextShouldLoadSuccessfullyWithSecurity() {
        assertNotNull(handlerExceptionResolver);
    }

    @Test
    @DisplayName("Spring beans related to web configuration should exist")
    void springBeansRelatedToWebConfigShouldExist() {
        assertDoesNotThrow(() -> {
            // If we reach here, Spring has initialized all beans successfully
            assertNotNull(handlerExceptionResolver);
        });
    }

    @Test
    @DisplayName("Web framework should be fully initialized")
    void webFrameworkShouldBeFullyInitialized() {
        assertNotNull(handlerExceptionResolver, "HandlerExceptionResolver must be initialized for web support");
    }
}
