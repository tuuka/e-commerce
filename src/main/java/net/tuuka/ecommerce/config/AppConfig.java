package net.tuuka.ecommerce.config;

import net.tuuka.ecommerce.controller.ProductRestController;
import net.tuuka.ecommerce.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.HypermediaRestTemplateConfigurer;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @Bean
    public RepresentationModelProcessor<PagedModel<EntityModel<Product>>> representationModelProcessor() {
    /*
    Dont replace it with lambda!!!!
    https://stackoverflow.com/questions/43428862/spring-data-rest-adding-link-to-root-repositorylinksresource-cannot-be-cast-to
    */
        return new RepresentationModelProcessor<PagedModel<EntityModel<Product>>>() {
            @Override
            public PagedModel<EntityModel<Product>> process(PagedModel<EntityModel<Product>> model) {
                Method method;
                try {
                    method = ProductRestController.class.getMethod("search", String.class, String.class, Pageable.class);
                    String methodParams = Arrays.stream(method.getParameterAnnotations())
                            .flatMap(Arrays::stream).filter(a -> a instanceof RequestParam).map(a -> {
                                String name = ((RequestParam) a).name();
                                return String.format("%s={%s}", name, name);
                            })
                            .collect(Collectors.joining("&"));
                    URI methodInvocationUri = linkTo(methodOn(ProductRestController.class).search(null, null, null))
                            .withRel("search").toUri();
                    Link link = Link.of(UriTemplate.of(methodInvocationUri + "?" + methodParams), "search");
                    List<Link> links = new ArrayList<>(model.getLinks().toList());
                    links.add(link);
                    model.removeLinks();
                    model.add(links);
                } catch (Exception e) {
                    return model;
                }

                return model;
            }
        };
    }

}
