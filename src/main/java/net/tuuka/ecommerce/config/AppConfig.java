package net.tuuka.ecommerce.config;

import net.tuuka.ecommerce.entity.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.AffordanceModel.InputPayloadMetadata;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.PropertyUtils;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;
import org.springframework.web.filter.ForwardedHeaderFilter;

import java.util.Arrays;
import java.util.List;


@Configuration
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL_FORMS)
public class AppConfig {

    /* https://docs.spring.io/spring-hateoas/docs/current/reference/html/#server.link-builder.forwarded-headers */
//    @Bean
//    ForwardedHeaderFilter forwardedHeaderFilter() {
//        return new ForwardedHeaderFilter();
//    }



}
