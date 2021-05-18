package net.tuuka.ecommerce.controller.assembler;

import lombok.var;
import net.tuuka.ecommerce.controller.ProductCategoryRestController;
import net.tuuka.ecommerce.entity.ProductCategory;
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
                .getCategoryById(entity.getId())).withSelfRel());
        if (entity.getProducts() != null)
            entityModel.add(linkTo(methodOn(controllerClass)
                    .getCategoryProducts(entity.getId())).withRel("products"));
        entityModel.add(linkTo(methodOn(controllerClass)
                .getAllCategories()).withRel("categories"));

        return entityModel;

    }

    @Override
    @NonNull
    public CollectionModel<EntityModel<ProductCategory>> toCollectionModel(@NonNull Iterable<? extends ProductCategory> entities) {

        var collectionModel = _toCollectionModel(entities);

        collectionModel.add(linkTo(methodOn(controllerClass).getAllCategories()).withSelfRel()
                , linkTo(methodOn(controllerClass).search(null)).withRel("search")
        );

        return collectionModel;

    }

    public CollectionModel<EntityModel<ProductCategory>> toSearchCollectionModel(Iterable<? extends ProductCategory> entities,
                                                                                 String name) {

        var collectionModel = _toCollectionModel(entities);

        collectionModel.add(linkTo(methodOn(controllerClass).search(name)).withSelfRel(),
                linkTo(methodOn(controllerClass).getAllCategories()).withRel("categories"));

        return collectionModel;

    }

    private CollectionModel<EntityModel<ProductCategory>> _toCollectionModel(Iterable<? extends ProductCategory> entities) {
        return StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
    }

}
