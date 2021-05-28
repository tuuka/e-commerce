package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.util.ProductCategoryModelAssembler;
import net.tuuka.ecommerce.controller.util.ProductModelAssembler;
import net.tuuka.ecommerce.controller.dto.CategoryRequestRepresentation;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.service.ProductCategoryService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/categories", produces = {"application/hal+json"})
public class ProductCategoryRestController {

    private final ProductCategoryService categoryService;
    private final ProductCategoryModelAssembler categoryAssembler;
    private final ProductModelAssembler productAssembler;

    @GetMapping
    public CollectionModel<?> getCategories() {
        return categoryAssembler.toCollectionModel(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public EntityModel<?> getCategory(@PathVariable("id") Long id) {
        return categoryAssembler.toModel(categoryService.findById(id));
    }

    @GetMapping("/{id}/products")
    public CollectionModel<?> getProducts(@PathVariable("id") Long id) {
        return productAssembler.toCollectionModel(categoryService.findById(id).getProducts());
    }

    @GetMapping("/search")
    public EntityModel<?> search(@RequestParam("name") String name) {
        return categoryAssembler.toModel(categoryService.findByName(name));
    }

    @PostMapping()
    public ResponseEntity<?> saveCategory(
            @RequestBody @Valid CategoryRequestRepresentation categoryRepresentation) {
        EntityModel<ProductCategory> categoryModel = categoryAssembler
                .toModel(categoryService.save(categoryRepresentation.getCategory()));
        return ResponseEntity.created(categoryModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(categoryModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable("id") Long id,
                                            @RequestBody @Valid CategoryRequestRepresentation categoryRepresentation) {

        categoryRepresentation.setId(id);
        EntityModel<ProductCategory> categoryModel = categoryAssembler
                .toModel(categoryService.update(categoryRepresentation.getCategory()));
        return ResponseEntity.ok().location(categoryModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(categoryModel);
    }

    @DeleteMapping("/{id}")
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
