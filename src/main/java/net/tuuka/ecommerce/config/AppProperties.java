package net.tuuka.ecommerce.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

/* Using with Spring Boot Configuration Processor to generate metadata for custom
    properties in application.properties file */

@Configuration
@ConfigurationProperties(prefix = "app")
@Setter
@Getter
public class AppProperties {

    private Map<String, String> api;
    private String[] api_cross_origins;
    private Boolean fill_products;

    private final Test test = new Test();
    private final AlpsProps alps = new AlpsProps();
    private final Security security = new Security();

    @Setter
    @Getter
    public static class Test {
        private Boolean integration_test_enabled;
    }

    @Setter
    @Getter
    public static class Security {
        private Integer token_expires_time_min = 30;
        private String secret;
        private Boolean confirmation_email = true;
        private Root root = new Root();

    }

    @Setter
    @Getter
    public static class Root {
        private String email;
        private String password;
    }

    @Setter
    @Getter
    @PropertySource("classpath:alps.properties")
    @Configuration
    @ConfigurationProperties(prefix = "app.alps")
    public static class AlpsProps {
        private Map<String, String> doc;
        private Map<String, String> profileRelations;
    }

}
