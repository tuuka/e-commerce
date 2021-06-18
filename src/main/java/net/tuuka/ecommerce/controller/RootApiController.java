package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.util.AlpsHelper;
import net.tuuka.ecommerce.model.Product;
import net.tuuka.ecommerce.model.ProductCategory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.alps.Alps;
import org.springframework.hateoas.mediatype.alps.Format;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.hateoas.MediaTypes.ALPS_JSON_VALUE;
import static org.springframework.hateoas.mediatype.alps.Alps.descriptor;
import static org.springframework.hateoas.mediatype.alps.Alps.doc;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

//@CrossOrigin
@RestController
@RequestMapping(path = "/api", produces = {"application/hal+json"})
@RequiredArgsConstructor
public class RootApiController {

    //    private final LinkRelationProvider linkRelationProvider;
    private final AlpsHelper alpsHelper;

    @GetMapping
    public RepresentationModel<?> root() {
        return HalModelBuilder.emptyHalModel()
                .link(linkTo(ProductRestController.class).withRel("products"))
                .link(linkTo(ProductCategoryRestController.class).withRel("categories"))
                .link(linkTo(methodOn(RootApiController.class).profile()).withRel("profile"))
                .build();
    }

    @GetMapping(path = "/profile")
    public RepresentationModel<?> profile() {
        return HalModelBuilder.emptyHalModel()
                .link(linkTo(methodOn(this.getClass()).profile()).withSelfRel())
                // not found any simple common way to link to the method returning Alps
                .link(Link.of(ServletUriComponentsBuilder.fromCurrentRequest()
                        .pathSegment("category").build().toUriString(), "category"))
                .link(Link.of(ServletUriComponentsBuilder.fromCurrentRequest()
                        .pathSegment("product").build().toUriString(), "product"))
                .build();
    }

    @GetMapping(value = "/profile/category", produces = ALPS_JSON_VALUE)
    public Alps categoryProfile() {

        return Alps.alps()
                .doc(doc()
//                        .href(ServletUriComponentsBuilder.fromCurrentRequest()
//                        .build().toUriString())
                        .value("product category")
                        .format(Format.TEXT)
                        .build())
                .descriptor(Stream.concat(
                        Stream.of(
                                descriptor().id(alpsHelper.composeRepresentationString(ProductCategory.class).substring(1))
                                        .href(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString())
                                        .descriptor(alpsHelper.getClassFieldsDescriptors(ProductCategory.class))
                                        .build()
                        )
                        , Arrays.stream(ProductCategoryRestController.class.getDeclaredMethods())
                                .map(m -> alpsHelper.getMethodDescriptor(m, ProductCategory.class))
                ).collect(Collectors.toList()))
                .build();
    }

    @GetMapping(value = "/profile/product", produces = ALPS_JSON_VALUE)
    public Alps productProfile() {
        return Alps.alps()
                .doc(doc()
                        .value("product")
                        .format(Format.TEXT)
                        .build())
                .descriptor(Stream.concat(
                        Stream.of(
                                descriptor().id(alpsHelper.composeRepresentationString(Product.class).substring(1))
                                        .href(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString())
                                        .descriptor(alpsHelper.getClassFieldsDescriptors(Product.class))
                                        .build()
                        )
                        , Arrays.stream(ProductRestController.class.getDeclaredMethods())
                                .map(m -> alpsHelper.getMethodDescriptor(m, Product.class))
                ).collect(Collectors.toList()))
                .build();
    }


}