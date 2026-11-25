package com.crimsonlogic.open_petal_backend.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import java.util.Properties;

class ProfileConfigurationTest {

    @Test
    void developmentAndProductionProfilesUseDifferentDiagnosticsAndNoInlineCredentials() {
        Properties development = load("application-dev.yml");
        Properties production = load("application-prod.yml");

        assertEquals("DEBUG", development.getProperty("logging.level.com.crimsonlogic.open_petal_backend"));
        assertEquals("INFO", production.getProperty("logging.level.com.crimsonlogic.open_petal_backend"));
        assertEquals("update", development.getProperty("spring.jpa.hibernate.ddl-auto"));
        assertEquals("validate", production.getProperty("spring.jpa.hibernate.ddl-auto"));
        assertEquals("${DB_PASSWORD}", development.getProperty("spring.datasource.password"));
        assertEquals("${DB_PASSWORD}", production.getProperty("spring.datasource.password"));
        assertEquals("${JWT_SECRET}", load("application.yml").getProperty("jwt.secret"));
        assertEquals("never", load("application.yml").getProperty("server.error.include-stacktrace"));
        assertTrue(production.getProperty("spring.mail.password").startsWith("${MAIL_PASSWORD"));
    }

    private Properties load(String resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new ClassPathResource(resource));
        return factory.getObject();
    }
}
