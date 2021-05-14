package net.tuuka.ecommerce.controller.util;

import net.tuuka.ecommerce.controller.v2.ProductCategoryRestControllerV2;
import net.tuuka.ecommerce.controller.v2.ProductRestControllerV2;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductCategoryModelAssembler implements
        RepresentationModelAssembler<ProductCategory, EntityModel<ProductCategory>> {

    @Override
    @NonNull
    public EntityModel<ProductCategory> toModel(@NonNull ProductCategory entity) {

        EntityModel<ProductCategory> entityModel = EntityModel.of(entity);
        entityModel.add(linkTo(methodOn(ProductCategoryRestControllerV2.class)
                .getCategoryById(entity.getId())).withSelfRel());
        if (entity.getProducts().size()>0)
            entityModel.add(linkTo(methodOn(ProductCategoryRestControllerV2.class)
                    .getCategoryProducts(entity.getId())).withRel("products"));
        entityModel.add(linkTo(methodOn(ProductCategoryRestControllerV2.class)
                .getAllCategories()).withRel("all categories"));

        return entityModel;

    }

}
