package net.tuuka.ecommerce.controller.util;

import net.tuuka.ecommerce.controller.v1.ProductRestController;
import net.tuuka.ecommerce.entity.Product;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductModelAssembler implements
        RepresentationModelAssembler<Product, EntityModel<Product>> {
    @Override
    public EntityModel<Product> toModel(Product product) {
        return EntityModel.of(product,
                linkTo(methodOn(ProductRestController.class)
                        .getProductById(product.getId())).withSelfRel(),
                linkTo(methodOn(ProductRestController.class)
                        .getAllProducts()).withRel("products"));
    }
}
