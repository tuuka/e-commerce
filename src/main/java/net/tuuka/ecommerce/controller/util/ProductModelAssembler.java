package net.tuuka.ecommerce.controller.util;

import net.tuuka.ecommerce.controller.ProductRestController;
import net.tuuka.ecommerce.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductModelAssembler implements
        RepresentationModelAssembler<Product, EntityModel<Product>> {

    private final Class<ProductRestController> controllerClass = ProductRestController.class;

    @Override
    @NonNull
    public EntityModel<Product> toModel(@NonNull Product entity) {

        EntityModel<Product> entityModel = EntityModel.of(entity);

        entityModel.add(linkTo(methodOn(controllerClass).getProductById(entity.getId())).withSelfRel());
        if (entity.getCategory() != null)
            entityModel.add(linkTo(methodOn(controllerClass)
                    .getProductCategory(entity.getId())).withRel("category"));
        entityModel.add(linkTo(methodOn(controllerClass).getAllProducts(Pageable.unpaged())).withRel("products"));

        return entityModel;

    }

}
