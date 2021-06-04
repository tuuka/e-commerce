package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.dto.ProductRequestRepresentation;
import net.tuuka.ecommerce.controller.util.ProductCategoryModelAssembler;
import net.tuuka.ecommerce.controller.util.ProductModelAssembler;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/products", produces = {"application/hal+json"})
public class ProductRestController {

    private final ProductService productService;
    private final ProductModelAssembler productAssembler;
    private final ProductCategoryModelAssembler categoryAssembler;
    private final PagedResourcesAssembler<Product> pagedResourcesAssembler;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<Product>>> getAllProducts(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok().body(pagedResourcesAssembler
                .toModel(productService.findAll(pageable),productAssembler));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public EntityModel<?> getProductById(@PathVariable("id") Long id) {
        return productAssembler.toModel(productService.findById(id));
    }

    @GetMapping("/{id}/category")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getProductCategory(@PathVariable("id") Long id) {
        ProductCategory category = productService.findById(id).getCategory();
        if (category == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok().body(categoryAssembler.toModel(productService.findById(id).getCategory()));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> search(@RequestParam(name = "sku", defaultValue = "") String sku,
                                     @RequestParam(name = "name", defaultValue = "") String name,
                                     @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok().body(pagedResourcesAssembler
                .toModel(productService.findAllBySkuOrName(sku, name, pageable),productAssembler));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveProduct(@RequestBody @Valid ProductRequestRepresentation productRequestRepresentation) {
        EntityModel<Product> productModel = productAssembler
                .toModel(productService.save(productRequestRepresentation.getProduct()));
        return ResponseEntity.created(productModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(productModel);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateProduct(@RequestBody @Valid ProductRequestRepresentation productRequestRepresentation,
                                           @PathVariable("id") Long id) {
        Product product = productRequestRepresentation.getProduct();
        product.setId(id);
        EntityModel<Product> productModel = productAssembler.toModel(productService.update(product));
        return ResponseEntity.ok().location(productModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(productModel);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EntityModel<?> deleteProductById(@PathVariable("id") Long id) {
        return productAssembler.toModel(productService.deleteById(id));
    }

}
