package net.tuuka.ecommerce.controller.v2;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.util.ProductModelAssembler;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.service.ProductService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "${app.api.path}" + "/v2/products", produces = { "application/hal+json" })
public class ProductRestControllerV2 {

    private final ProductService productService;
    private final ProductModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<Product>> getAllProducts() {
        return assembler.toCollectionModel(productService.getAll());
    }

    @GetMapping("/{id}")
    public EntityModel<Product> getProductById(@PathVariable long id) {
        return assembler.toModel(productService.getById(id));
    }

    @GetMapping("/search")
    public List<Product> getProductBySku(@RequestParam(name = "sku", defaultValue = "") String sku,
                                         @RequestParam(value = "name", defaultValue = "") String name) {
        return productService.findAllBySkuOrName(sku, name);
    }

    @PostMapping()
    public ResponseEntity<EntityModel<Product>> saveProduct(@RequestBody Product product) {
        EntityModel<Product> productModel = assembler.toModel(productService.save(product));
        return ResponseEntity.created(productModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(productModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Product>> updateProduct(@PathVariable long id, @RequestBody Product product) {
        product.setId(id);
        EntityModel<Product> productModel = assembler.toModel(productService.update(product));
        return ResponseEntity.ok().location(productModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(productModel);
    }

    @DeleteMapping("/{id}")
    public EntityModel<Product> deleteProductById(@PathVariable long id) {
        return assembler.toModel(productService.deleteById(id));
    }

}
