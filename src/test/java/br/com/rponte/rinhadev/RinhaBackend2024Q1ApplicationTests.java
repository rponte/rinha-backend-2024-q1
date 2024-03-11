package br.com.rponte.rinhadev;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@SpringBootTest(properties = {
        "spring.sql.init.mode=never" // disables schema creation
})
class RinhaBackend2024Q1ApplicationTests {

    @Autowired
    private Environment env;

    /**
     * Coffee Studio's <b>java-springboot-restapi-base-plugin</b> configuration:<br/><br/>
     *
     * That's a simple way to guarantee that essential configuration properties set by this plugin are correctly defined
     * as expected in both {@code application.yaml} and {@code application-test.yaml} files.
     */
    @Test
    @DisplayName("validate 'java-springboot-restapi-base-plugin' configuration properties")
    void validateBasePluginConfig() {
        assertAll("server config",
                () -> _assertPropertyEquals("8080", "server.port"),
                () -> _assertPropertyEquals("/", "server.servlet.context-path"),
                () -> _assertPropertyEquals("always", "server.error.include-message"),
                () -> _assertPropertyEquals("always", "server.error.include-binding-errors"),
                () -> _assertPropertyEquals("on_param", "server.error.include-stacktrace"),
                () -> _assertPropertyEquals("false", "server.error.include-exception")
        );
        assertAll("spring config",
                () -> _assertPropertyEquals("rinhadev", "spring.application.name"),
                () -> _assertPropertyEquals("ALWAYS", "spring.output.ansi.enabled")
        );
        assertAll("actuator config",
                () -> _assertPropertyEquals("*", "management.endpoints.jmx.exposure.include"),
                () -> _assertPropertyEquals("health,metrics", "management.endpoints.web.exposure.include"),
                () -> _assertPropertyEquals("always", "management.endpoint.health.show-details"),
                () -> _assertPropertyEquals("always", "management.endpoint.health.show-components"),
                () -> _assertPropertyEquals("true", "management.endpoint.health.probes.enabled"),
                () -> _assertPropertyEquals("true", "management.endpoint.health.probes.add-additional-paths")
        );
        assertAll("jackson config",
                () -> _assertPropertyEquals("true", "spring.jackson.serialization.indent_output")
        );
    }


    /**
     * Coffee Studio's <b>java-spring-data-jpa-plugin</b> configuration:<br/><br/>
     *
     * That's a simple way to guarantee that essential configuration properties set by this plugin are correctly defined
     * as expected in both {@code application.yaml} and {@code application-test.yaml} files.
     */
    @Test
    @DisplayName("validate 'java-spring-data-jpa-plugin' configuration properties")
    void validateSpringDataJpaPluginConfig() {
        assertAll("spring datasource config",
                () -> _assertPropertyEquals("org.testcontainers.jdbc.ContainerDatabaseDriver", "spring.datasource.driverClassName"),
                () -> _assertPropertyEquals("jdbc:tc:postgresql:16.2:////test_db", "spring.datasource.url"),
                () -> _assertPropertyEquals("postgres", "spring.datasource.username"),
                () -> _assertPropertyEquals("postgres", "spring.datasource.password")
        );
        assertAll("spring datasource config - connection pool (HikariCP)",
                () -> _assertPropertyEquals("false", "spring.datasource.hikari.auto-commit"),
                () -> _assertPropertyEquals("20", "spring.datasource.hikari.maximum-pool-size"),
                () -> _assertPropertyEquals("1000", "spring.datasource.hikari.connection-timeout"),
                () -> _assertPropertyEquals("500", "spring.datasource.hikari.validation-timeout"),
                () -> _assertPropertyEquals("1800000", "spring.datasource.hikari.max-lifetime"),
                () -> _assertPropertyEquals("60000", "spring.datasource.hikari.leak-detection-threshold")
        );
        assertAll("spring jpa config",
                () -> _assertPropertyEquals("false", "spring.jpa.generate-ddl"),
                () -> _assertPropertyEquals("true", "spring.jpa.show-sql"),
                () -> _assertPropertyEquals("false", "spring.jpa.open-in-view"),
                () -> _assertPropertyEquals("update", "spring.jpa.hibernate.ddl-auto"),
                () -> _assertPropertyEquals("true", "spring.jpa.properties.hibernate.format_sql"),
                () -> _assertPropertyEquals("UTC", "spring.jpa.properties.hibernate.jdbc.time_zone"),
                () -> _assertPropertyEquals("15", "spring.jpa.properties.hibernate.jdbc.batch_size"),
                () -> _assertPropertyEquals("true", "spring.jpa.properties.hibernate.jdbc.order_inserts"),
                () -> _assertPropertyEquals("true", "spring.jpa.properties.hibernate.jdbc.order_updates"),
                () -> _assertPropertyEquals("true", "spring.jpa.properties.hibernate.jdbc.batch_versioned_data"),
                () -> _assertPropertyEquals("true", "spring.jpa.properties.hibernate.connection.provider_disables_autocommit"),
                () -> _assertPropertyEquals("true", "spring.jpa.properties.hibernate.query.in_clause_parameter_padding"),
                () -> _assertPropertyEquals("true", "spring.jpa.properties.hibernate.query.fail_on_pagination_over_collection_fetch"),
                () -> _assertPropertyEquals("1024", "spring.jpa.properties.hibernate.query.plan_cache_max_size")
        );
    }

    private void _assertPropertyEquals(Object expected, String propertyName) {
        assertEquals(expected, env.getProperty(propertyName), propertyName);
    }

}