package com.techmate.techmate.security;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TokenSecretValidator {
    private static final Logger log = LoggerFactory.getLogger(TokenSecretValidator.class);

    private final Environment env;

    public TokenSecretValidator(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void validate() {
        String secret = System.getenv("JWT_SECRET");

        String[] activeProfiles = env.getActiveProfiles();
        String profile = (activeProfiles != null && activeProfiles.length > 0) ? activeProfiles[0]
                : env.getProperty("spring.profiles.active");

        boolean isTestOrDev = profile != null && (profile.equalsIgnoreCase("test") || profile.equalsIgnoreCase("dev"));

        if ((secret == null || secret.isBlank()) && !isTestOrDev) {
            log.error("Missing JWT_SECRET environment variable and active profile is '{}'. Aborting startup.", profile);
            throw new IllegalStateException("Missing JWT_SECRET environment variable. Set JWT_SECRET in production.");
        }

        if (secret == null || secret.isBlank()) {
            log.warn("No JWT_SECRET provided for profile '{}'. Using test/dev fallback is allowed only for development and tests.", profile);
        } else {
            log.debug("JWT secret found in environment for profile '{}'.", profile);
        }
    }
}
