package net.tuuka.ecommerce.controller.assembler;

import lombok.SneakyThrows;
import lombok.var;
import net.tuuka.ecommerce.controller.ProductRestController;
import net.tuuka.ecommerce.entity.Product;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductModelAssembler implements
        RepresentationModelAssembler<Product, EntityModel<Product>> {

    private final Class<ProductRestController> controllerClass = ProductRestController.class;

    @Override
    @NonNull
    public EntityModel<Product> toModel(@NonNull Product entity) {

        var entityModel = EntityModel.of(entity);

        entityModel.add(linkTo(methodOn(controllerClass)
                .getProductById(entity.getId())).withSelfRel());
        if (entity.getCategory() != null)
            entityModel.add(linkTo(methodOn(controllerClass)
                    .getProductCategory(entity.getId())).withRel("category"));
        entityModel.add(linkTo(methodOn(controllerClass)
                .getAllProducts()).withRel("products"));

        return entityModel;

    }

    @SneakyThrows
    @Override
    @NonNull
    public CollectionModel<EntityModel<Product>> toCollectionModel(@NonNull Iterable<? extends Product> entities) {

        var collectionModel = _toCollectionModel(entities);

        collectionModel.add(linkTo(methodOn(controllerClass).getAllProducts()).withSelfRel());

        // add search templated link with (Spring Hateoas adds templated links only to EntityModel)
        Method method = ProductRestController.class.getMethod("search", String.class, String.class);

        String methodParams = "?" + Arrays.stream(method.getParameterAnnotations())
                .flatMap(Arrays::stream).filter(a->a instanceof RequestParam).map(a->{
                    String name = ((RequestParam)a).name();
                    return String.format("%s={%s}",name, name);
                })
                .collect(Collectors.joining("&"));

        URI methodInvocationUri = linkTo(methodOn(controllerClass).search(null, null))
                .withRel("search").toUri();

        collectionModel.add(Link.of(UriTemplate.of(methodInvocationUri + methodParams), "search"));
        return collectionModel;

    }

    public CollectionModel<EntityModel<Product>> toSearchCollectionModel(Iterable<? extends Product> entities,
                                                                         String sku, String name) {

        var collectionModel = _toCollectionModel(entities);

        collectionModel.add(linkTo(methodOn(controllerClass).search(sku, name)).withSelfRel(),
                linkTo(methodOn(controllerClass).getAllProducts()).withRel("products"));

        return collectionModel;

    }

    private CollectionModel<EntityModel<Product>> _toCollectionModel(Iterable<? extends Product> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }

}
