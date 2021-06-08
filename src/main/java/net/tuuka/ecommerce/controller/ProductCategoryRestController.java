package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.dto.CategoryRequestRepresentation;
import net.tuuka.ecommerce.controller.util.ProductCategoryModelAssembler;
import net.tuuka.ecommerce.controller.util.ProductModelAssembler;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.service.ProductCategoryService;
import net.tuuka.ecommerce.service.ProductService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

//@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/categories", produces = {"application/hal+json"})
public class ProductCategoryRestController {

    private final ProductCategoryService categoryService;
    private final ProductService productService;
    private final ProductCategoryModelAssembler categoryAssembler;
    private final ProductModelAssembler productAssembler;
    private final PagedResourcesAssembler<Product> pagedResourcesAssembler;

    @GetMapping
//    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public CollectionModel<?> getCategories() {
        return categoryAssembler
                .toCollectionModel(categoryService.findAll());
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public EntityModel<?> getCategory(@PathVariable("id") Long id) {
        return categoryAssembler.toModel(categoryService.findById(id));
    }

    @GetMapping("/{id}/products")
//    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<Product>>> getProducts(
            @PathVariable("id") Long id,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok().body(pagedResourcesAssembler
                .toModel(productService.findAllByCategory(id, pageable),
                        productAssembler));
    }

    @GetMapping("/search")
//    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public EntityModel<?> search(@RequestParam("name") String name) {
        return categoryAssembler.toModel(categoryService.findByName(name));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveCategory(
            @RequestBody @Valid CategoryRequestRepresentation categoryRepresentation) {
        EntityModel<ProductCategory> categoryModel = categoryAssembler
                .toModel(categoryService.save(categoryRepresentation.getCategory()));
        return ResponseEntity.created(categoryModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(categoryModel);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable("id") Long id,
                                            @RequestBody @Valid CategoryRequestRepresentation categoryRepresentation) {

        categoryRepresentation.setId(id);
        EntityModel<ProductCategory> categoryModel = categoryAssembler
                .toModel(categoryService.update(categoryRepresentation.getCategory()));
        return ResponseEntity.ok().location(categoryModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(categoryModel);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCategory(@PathVariable("id") Long id,
                                            @RequestParam(name = "force",
                                                    defaultValue = "false")
                                                    boolean force) {
        EntityModel<ProductCategory> categoryModel = categoryAssembler.toModel(
                force ? categoryService.forceDeleteCategory(id) : categoryService.deleteById(id)
        );
        return ResponseEntity.ok().location(categoryModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(categoryModel);
    }

}
