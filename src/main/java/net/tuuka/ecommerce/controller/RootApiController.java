package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.util.AlpsHelper;
import net.tuuka.ecommerce.entity.ProductCategory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.alps.Alps;
import org.springframework.hateoas.mediatype.alps.Descriptor;
import org.springframework.hateoas.mediatype.alps.Format;
import org.springframework.hateoas.mediatype.alps.Type;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.hateoas.MediaTypes.ALPS_JSON_VALUE;
import static org.springframework.hateoas.mediatype.PropertyUtils.getExposedProperties;
import static org.springframework.hateoas.mediatype.alps.Alps.descriptor;
import static org.springframework.hateoas.mediatype.alps.Alps.doc;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
                        .pathSegment("categories").build().toUriString(), "categories"))
                .link(Link.of(ServletUriComponentsBuilder.fromCurrentRequest()
                        .pathSegment("products").build().toUriString(), "products"))
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
                                descriptor().id(alpsHelper.getRepresentationString(ProductCategory.class))
                                        .href(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString())
                                        .descriptor(
                                                getExposedProperties(ProductCategory.class).stream()
                                                        .map(property -> {
                                                            Descriptor.DescriptorBuilder builder = descriptor()
                                                                    .id(property.getName())
                                                                    .name(property.getName())
                                                                    .type(Type.SEMANTIC);
                                                            if (property.getName().equals("products")) {
                                                                builder.type(Type.SAFE).rt(
                                                                        alpsHelper.getHrefToRestMethod(this.getClass(),
                                                                                "productProfile"));
                                                            }
                                                            return builder.build();
                                                        })
                                                        .collect(Collectors.toList()))
                                        .build()
                        )
                        , Arrays.stream(ProductCategoryRestController.class.getDeclaredMethods())
                                .map(m -> alpsHelper.getMethodDescriptor(m, ProductCategory.class))
                ).collect(Collectors.toList()))
                .build();
    }

    @GetMapping(value = "/profile/product", produces = ALPS_JSON_VALUE)
    public Alps productProfile() {
        return Alps.alps().build();
    }


}

/*
        return Alps.alps()
                .doc(doc()
//                        .href(ServletUriComponentsBuilder.fromCurrentRequest()
//                        .build().toUriString())
                .value("product category")
                .format(Format.TEXT)
                .build())
                .descriptor(Collections.singletonList(descriptor().id("category" + "-representation")
                .href(ServletUriComponentsBuilder.fromCurrentRequest()
                .build().toUriString()).descriptor(
                getExposedProperties(Product.class).stream()
        .map(property -> Descriptor.builder()
        .id("class field [" + property.getName() + "]")
        .name(property.getName())
        .type(Type.SEMANTIC)
        .ext(Ext.builder()
        .id("ext [" + property.getName() + "]")
        .href("https://example.org/samples/ext/" + property.getName())
        .value("value goes here")
        .build())
        .rt("rt for [" + property.getName() + "]")
        .descriptor(Collections.singletonList(Descriptor.builder().id("embedded").build()))
        .build())
        .collect(Collectors.toList()))
        .build())).build();

 */