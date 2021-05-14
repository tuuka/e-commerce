package net.tuuka.ecommerce.controller.util;

import lombok.var;
import net.tuuka.ecommerce.controller.hateoas.ProductPayloadMetadata;
import net.tuuka.ecommerce.controller.model.ProductRepresentation;
import net.tuuka.ecommerce.controller.v2.ProductRestControllerV2;
import net.tuuka.ecommerce.entity.Product;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductModelAssembler implements
        RepresentationModelAssembler<Product, EntityModel<Product>> {

    private final Class<ProductRestControllerV2> controllerClass = ProductRestControllerV2.class;

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
    public CollectionModel<EntityModel<Product>> toCollectionModel(Iterable<? extends Product> entities) {

        var collectionModel =
                StreamSupport.stream(entities.spliterator(), false)
                        .map(this::toModel)
                        .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));

        var link = Affordances.of(linkTo(methodOn(controllerClass).getAllProducts())
                .withSelfRel())
                .afford(HttpMethod.POST)
                .withInput(ProductRepresentation.class)
                .withOutput(Product.class)
                .withName("newProduct")
                .toLink();

        collectionModel.add(link,
                linkTo(methodOn(controllerClass).search(null, null)).withRel("search"));

//        var link = Affordances.of(linkTo(methodInvocation).withSelfRel())
////                .afford(HttpMethod.POST)
////                .withInputAndOutput(Product.class)
////                .withName("createProduct")
//
//                .afford(HttpMethod.GET)
//                .withOutput(Product.class)
//                .addParameters(
//                        QueryParameter.optional("sku"),
//                        QueryParameter.optional("name"))
//                .withName("search")
//                .toLink();
//
//        collectionModel.add(link);

        return collectionModel;

    }

    private EntityModel<Product> _toModel(Product entity, boolean affordsIncluded) {

        var entityModel = EntityModel.of(entity);

        if (affordsIncluded) {
            Link link = Affordances.of(linkTo(methodOn(controllerClass)
                    .getProductById(entity.getId())).withSelfRel())
                    .afford(HttpMethod.PUT)
//                .withInput(productPayloadMetadata)
                    .withInput(ProductRepresentation.class)
                    .withOutput(Product.class)
                    .withName("updateProduct")
                    .andAfford(HttpMethod.DELETE)
                    .toLink();
            entityModel.add(link);

            if (entity.getCategory() != null)
                entityModel.add(linkTo(methodOn(controllerClass)
                        .getProductCategory(entity.getId())).withRel("category"));
            entityModel.add(linkTo(methodOn(controllerClass)
                    .getAllProducts()).withRel("products"));
        } else {
            entityModel.add(linkTo(methodOn(controllerClass)
                    .getProductById(entity.getId())).withSelfRel());
        }

        return entityModel;

    }


}
