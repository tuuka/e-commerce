package net.tuuka.ecommerce.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Objects;


// Used only if there is a strong irresistible desire to test some stuff
// on an external database.

@TestConfiguration
@EnableTransactionManagement
@ActiveProfiles("test")
@EnabledIf(value = "${app.test.rest_integration_test_enabled}", loadContext = true)
public class TestConfig {

    @Bean
//    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties testDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.configuration")
    public DataSource testDataSource() {
        return testDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean testEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(testDataSource())
                .packages("net.tuuka.ecommerce")
                .build();
    }

    @Bean
    public PlatformTransactionManager testTransactionManager(
            LocalContainerEntityManagerFactoryBean testEntityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(testEntityManagerFactory.getObject()));
    }

}
