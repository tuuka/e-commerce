package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import lombok.var;
import net.tuuka.ecommerce.entity.BaseEntity;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.alps.Alps;
import org.springframework.hateoas.mediatype.alps.Descriptor;
import org.springframework.hateoas.mediatype.alps.Format;
import org.springframework.hateoas.mediatype.alps.Type;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
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

    private final LinkRelationProvider linkRelationProvider;


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

    @GetMapping(value = "/profile/categories", produces = ALPS_JSON_VALUE)
    public Alps categoriesProfile() {

        String categoryRepresentation = "category-representation";

        return Alps.alps()
                .doc(doc()
//                        .href(ServletUriComponentsBuilder.fromCurrentRequest()
//                        .build().toUriString())
                        .value("product category")
                        .format(Format.TEXT)
                        .build())
                .descriptor(Stream.concat(
                        Stream.of(
                                descriptor().id(categoryRepresentation)
                                        .href(ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString())
                                        .descriptor(
                                                getExposedProperties(ProductCategory.class).stream()
                                                        .map(property -> descriptor()
                                                                .id(property.getName())
                                                                .name(property.getName())
                                                                .type(Type.SEMANTIC)
                                                                .build())
                                                        .collect(Collectors.toList()))
                                        .build()
                        ),
                        Arrays.stream(ProductCategoryRestController.class.getDeclaredMethods())
                                .map(m -> this.getMethodDescriptor(m, ProductCategory.class))
                ).collect(Collectors.toList()))
                .build();
    }

    public Descriptor getMethodDescriptor(Method method, Class<? extends BaseEntity> entityClass) {

        Class<?> methodClass = method.getDeclaringClass();

        AnnotationAttributes attr = MergedAnnotations.from(method).get(RequestMapping.class).asAnnotationAttributes();
        if (attr.size() == 0) return null;

        String name = Iterable.class.isAssignableFrom(method.getReturnType()) ?
                linkRelationProvider.getCollectionResourceRelFor(entityClass).value() :
                linkRelationProvider.getItemResourceRelFor(entityClass).value();
        if (ProductCategory.class.isAssignableFrom(entityClass) &&
                method.getName().contains("Product")) name = "products";
        if (Product.class.isAssignableFrom(entityClass) &&
                method.getName().contains("Categor")) name = "category";

        String[] path = attr.getStringArray("path");
        String href = linkTo(methodClass).toUriComponentsBuilder()
                .pathSegment(path.length > 0 ? path[0].substring(1) : "")
                .build().toUriString();
        RequestMethod requestMethod = ((RequestMethod[]) attr.get("method"))[0];
        String id = method.getName().replaceAll("(?<![_-]|^)(?=[A-Z])", "-").toLowerCase();
        Type type = Type.IDEMPOTENT;
        if (requestMethod.equals(RequestMethod.POST)) type = Type.UNSAFE;
        if (requestMethod.equals(RequestMethod.GET)) type = Type.SAFE;

        return descriptor().id(id).name(name).href(href).type(type).build();
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