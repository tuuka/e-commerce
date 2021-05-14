package net.tuuka.ecommerce.controller.v2;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.model.ProductRepresentation;
import net.tuuka.ecommerce.controller.util.ProductCategoryModelAssembler;
import net.tuuka.ecommerce.controller.util.ProductModelAssembler;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.service.ProductService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
//@RequestMapping(path = "/api/products", produces = {"application/prs.hal-forms+json", "application/hal+json" })
@RequestMapping(path = "/api/products")
public class ProductRestControllerV2 {

    private final ProductService productService;
    private final ProductModelAssembler productAssembler;
    private final ProductCategoryModelAssembler categoryAssembler;

    @GetMapping
    public CollectionModel<?> getAllProducts() {
        return productAssembler.toCollectionModel(productService.getAll());
    }

    @GetMapping("/{id}")
    public EntityModel<?> getProductById(@PathVariable long id) {
        return productAssembler.toModel(productService.getById(id));
    }

    @GetMapping("/{id}/category")
    public EntityModel<?> getProductCategory(@PathVariable long id) {
        return categoryAssembler.toModel(productService.getById(id).getCategory());
    }

    @GetMapping("/search")
    public CollectionModel<?> search(@RequestParam(name = "sku", defaultValue = "") String sku,
                                     @RequestParam(value = "name", defaultValue = "") String name) {
        return productAssembler.toCollectionModel(productService.findAllBySkuOrName(sku, name));
    }

    @PostMapping()
    public ResponseEntity<?> saveProduct(@RequestBody @Valid ProductRepresentation productRepresentation) {
        EntityModel<Product> productModel = productAssembler
                .toModel(productService.save(productRepresentation.getProduct()));
        return ResponseEntity.created(productModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(productModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@RequestBody @Valid ProductRepresentation productRepresentation,
                                           @PathVariable Long id) {
        Product product = productRepresentation.getProduct();
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
