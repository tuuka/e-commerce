package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.service.ProductCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.path}" + "/v1/product_categories")
public class ProductCategoryRestController {

    private final ProductCategoryService categoryService;

    @GetMapping
    public List<ProductCategory> getAllCategories() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    public ProductCategory getCategoryById(@PathVariable long id) {
        return categoryService.getById(id);
    }

    @GetMapping("/search")
    public ProductCategory getCategoryByName(@RequestParam("name") String name) {
        System.out.println();
        return categoryService.findByName(name);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ProductCategory saveCategory(@RequestBody ProductCategory category) {
        return categoryService.save(category);
    }

    @PutMapping("/{id}")
    public ProductCategory updateCategory(@PathVariable long id, @RequestBody ProductCategory category) {
        category.setId(id);
        return categoryService.update(category);
    }

    @DeleteMapping("/{id}")
    public ProductCategory deleteCategoryById(@PathVariable long id,
                                              @RequestParam(name = "force",
                                                      defaultValue = "false")
                                                      boolean force) {
        return force ? categoryService.forceDeleteCategory(id) : categoryService.deleteById(id);
    }

}
