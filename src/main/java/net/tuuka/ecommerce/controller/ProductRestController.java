package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.assembler.ProductCategoryModelAssembler;
import net.tuuka.ecommerce.controller.assembler.ProductModelAssembler;
import net.tuuka.ecommerce.controller.dto.ProductRequestRepresentation;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.service.ProductService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/products", produces = {"application/hal+json"})
public class ProductRestController {

    private final ProductService productService;
    private final ProductModelAssembler productAssembler;
    private final ProductCategoryModelAssembler categoryAssembler;

    @GetMapping
    public CollectionModel<?> getAllProducts() {
        return productAssembler.toCollectionModel(productService.getAll());
    }

    @GetMapping("/{id}")
    public EntityModel<?> getProductById(@PathVariable Long id) {
        return productAssembler.toModel(productService.getById(id));
    }

    @GetMapping("/{id}/category")
    public ResponseEntity<?> getProductCategory(@PathVariable Long id) {
        ProductCategory category = productService.getById(id).getCategory();
        if (category == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(categoryAssembler.toModel(productService.getById(id).getCategory()));
    }

    @GetMapping("/search")
    public CollectionModel<?> search(@RequestParam(name = "sku", defaultValue = "") String sku,
                                     @RequestParam(name = "name", defaultValue = "") String name) {
        return productAssembler.toSearchCollectionModel(productService.findAllBySkuOrName(sku, name),
                sku, name);
    }

    @PostMapping()
    public ResponseEntity<?> saveProduct(@RequestBody @Valid ProductRequestRepresentation productRequestRepresentation) {
        EntityModel<Product> productModel = productAssembler
                .toModel(productService.save(productRequestRepresentation.getProduct()));
        return ResponseEntity.created(productModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(productModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@RequestBody @Valid ProductRequestRepresentation productRequestRepresentation,
                                           @PathVariable Long id) {
        Product product = productRequestRepresentation.getProduct();
        product.setId(id);
        EntityModel<Product> productModel = productAssembler.toModel(productService.update(product));
        return ResponseEntity.ok().location(productModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(productModel);
    }

    @DeleteMapping("/{id}")
    public EntityModel<?> deleteProductById(@PathVariable Long id) {
        return productAssembler.toModel(productService.deleteById(id));
    }

}
