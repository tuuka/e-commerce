package net.tuuka.ecommerce.controller.util;

import lombok.var;
import net.tuuka.ecommerce.controller.ProductRestController;
import net.tuuka.ecommerce.entity.Product;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

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

    @Override
    @NonNull
    public CollectionModel<EntityModel<Product>> toCollectionModel(@NonNull Iterable<? extends Product> entities) {

        var collectionModel = _toCollectionModel(entities);

        collectionModel.add(linkTo(methodOn(controllerClass).getAllProducts()).withSelfRel()
                , linkTo(methodOn(controllerClass).search(null, null)).withRel("search")
        );


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
