package net.tuuka.ecommerce.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/* Using with Spring Boot Configuration Processor to generate metadata for custom
    properties in application.properties file */

@Configuration
@ConfigurationProperties(prefix = "app")
@Setter
@Getter
public class AppProperties {

    private Map<String, String> api;

    private final Test test = new Test();

    @Setter
    @Getter
    public static class Test {
        private Boolean rest_integration_test_enabled;
    }

}
