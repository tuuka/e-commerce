package net.tuuka.ecommerce.util;

import net.tuuka.ecommerce.controller.ProductCategoryRestController;
import net.tuuka.ecommerce.model.product.ProductCategory;
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
public class ProductCategoryModelAssembler implements
        RepresentationModelAssembler<ProductCategory, EntityModel<ProductCategory>> {

    private final Class<ProductCategoryRestController> controllerClass = ProductCategoryRestController.class;

    @Override
    @NonNull
    public EntityModel<ProductCategory> toModel(@NonNull ProductCategory entity) {

        EntityModel<ProductCategory> entityModel = EntityModel.of(entity);
        entityModel.add(linkTo(methodOn(controllerClass)
                .getCategory(entity.getId())).withSelfRel());
        if (entity.getProducts() != null)
            entityModel.add(linkTo(methodOn(controllerClass)
                    .getProducts(entity.getId(), null)).withRel("products"));
        entityModel.add(linkTo(methodOn(controllerClass)
                .getCategories()).withRel("categories"));

        return entityModel;

    }

    @Override
    @NonNull
    public CollectionModel<EntityModel<ProductCategory>> toCollectionModel(@NonNull Iterable<? extends ProductCategory> entities) {

        CollectionModel<EntityModel<ProductCategory>> collectionModel = _toCollectionModel(entities);

        collectionModel.add(linkTo(methodOn(controllerClass).getCategories()).withSelfRel()
                , linkTo(methodOn(controllerClass).search(null)).withRel("search")
        );

        return collectionModel;

    }

    public CollectionModel<EntityModel<ProductCategory>> toSearchCollectionModel(Iterable<? extends ProductCategory> entities,
                                                                                 String name) {

        CollectionModel<EntityModel<ProductCategory>> collectionModel = _toCollectionModel(entities);

        collectionModel.add(linkTo(methodOn(controllerClass).search(name)).withSelfRel(),
                linkTo(methodOn(controllerClass).getCategories()).withRel("categories"));

        return collectionModel;

    }

    private CollectionModel<EntityModel<ProductCategory>> _toCollectionModel(Iterable<? extends ProductCategory> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }

}
