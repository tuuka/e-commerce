package net.tuuka.ecommerce.config;

import net.tuuka.ecommerce.controller.ProductRestController;
import net.tuuka.ecommerce.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.*;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.HypermediaRestTemplateConfigurer;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Configuration
//@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL_FORMS)
@EnableHypermediaSupport(type = {EnableHypermediaSupport.HypermediaType.HAL})
public class AppConfig {

    /* https://docs.spring.io/spring-hateoas/docs/current/reference/html/#server.link-builder.forwarded-headers */
//    @Bean
//    ForwardedHeaderFilter forwardedHeaderFilter() {
//        return new ForwardedHeaderFilter();
//    }

    @Bean
    RestTemplateCustomizer hypermediaRestTemplateCustomizer(@Autowired HypermediaRestTemplateConfigurer configurer) {
        return configurer::registerHypermediaTypes;
    }


    /* Replace/add a search link with parameters in product collectionModel */
//    @Bean
    // TODO: consider to remove it
    public RepresentationModelProcessor<CollectionModel<EntityModel<Product>>> productLinkProcessor() {

        return model -> {
            Method method;
            try {
                method = ProductRestController.class.getMethod("search", String.class, String.class);
                String searchParams = Arrays.stream(method.getDeclaredAnnotationsByType(RequestParam.class).clone())
                        .map(a -> String.format("%s={%s}", a.name(), a.name())).collect(Collectors.joining("&"));
                URI methodInvocationUri = linkTo(methodOn(ProductRestController.class).search(null, null)).toUri();
                Links links = model.getLinks().without(LinkRelation.of("search"));
                links.and(Link.of(UriTemplate.of(methodInvocationUri + "?" + searchParams), "search"));
                model.removeLinks();
                model.add(links);
            } catch (Exception e) {
                return model;
            }
            return model;
        };

    }

}
