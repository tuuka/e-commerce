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

    /**
    * Api properties
    * */
    private Map<String, String> api;

}
