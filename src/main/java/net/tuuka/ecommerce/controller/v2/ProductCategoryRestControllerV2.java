package net.tuuka.ecommerce.controller.v2;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.util.ProductCategoryModelAssembler;
import net.tuuka.ecommerce.controller.util.ProductModelAssembler;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.service.ProductCategoryService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/product_categories", produces = {"application/hal+json"})
public class ProductCategoryRestControllerV2 {

    private final ProductCategoryService categoryService;
    private final ProductCategoryModelAssembler categoryAssembler;
    private final ProductModelAssembler productAssembler;

    @GetMapping
    public CollectionModel<?> getAllCategories() {
        return categoryAssembler.toCollectionModel(categoryService.getAll());
    }

    @GetMapping("/{id}")
    public EntityModel<?> getCategoryById(@PathVariable long id) {
        return categoryAssembler.toModel(categoryService.getById(id));
    }

    @GetMapping("/{id}/products")
    public CollectionModel<?> getCategoryProducts(@PathVariable long id) {
        return productAssembler.toCollectionModel(categoryService.getById(id).getProducts());
    }

    @GetMapping("/search")
    public EntityModel<?> getCategoryByName(
            @RequestParam("name") String name) {
        return categoryAssembler.toModel(categoryService.findByName(name));
    }

    @PostMapping()
    public ResponseEntity<?> saveCategory(
            @RequestBody ProductCategory category) {
        EntityModel<ProductCategory> categoryModel = categoryAssembler.toModel(categoryService.save(category));
        return ResponseEntity.created(categoryModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(categoryModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable long id, @RequestBody ProductCategory category) {
        category.setId(id);
        EntityModel<ProductCategory> categoryModel = categoryAssembler.toModel(categoryService.update(category));
        return ResponseEntity.ok().location(categoryModel.getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(categoryModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable long id,
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
