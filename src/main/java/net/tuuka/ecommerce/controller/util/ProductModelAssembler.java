package net.tuuka.ecommerce.controller.util;

import net.tuuka.ecommerce.controller.v2.ProductRestControllerV2;
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
                linkTo(methodOn(ProductRestControllerV2.class)
                        .getProductById(product.getId())).withSelfRel(),
                linkTo(methodOn(ProductRestControllerV2.class)
                        .getAllProducts()).withRel("products"));
    }
}
