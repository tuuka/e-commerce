package net.tuuka.ecommerce.service;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.exception.ProductCategoryNotEmptyException;
import net.tuuka.ecommerce.exception.ProductCategoryNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;

    public List<ProductCategory> getAllCategories(){
        return categoryRepository.findAll();
    }

    public ProductCategory getCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(()->new ProductCategoryNotFoundException(
                        "Product category with id=" + id + " not found"));
    }


    public ProductCategory saveCategory(ProductCategory category) {
        Objects.requireNonNull(category);
        if (category.getId() != null) throw new IllegalStateException("Category id have to be null");
        return categoryRepository.save(category);
    }

    public ProductCategory updateCategory(ProductCategory category) {
        Objects.requireNonNull(category);
        if (category.getId() == null) throw new IllegalStateException("Category id must not be null");
        getCategoryById(category.getId());
        return categoryRepository.save(category);
    }

    public ProductCategory deleteCategory(long id) {
        ProductCategory foundCategory = getCategoryById(id);
        if (foundCategory.getProducts().size() > 0)
            throw new ProductCategoryNotEmptyException(String.format(
                    "Category with id=%d and name='%s' is not empty",
                    id, foundCategory.getName()));
        categoryRepository.deleteById(id);
        return foundCategory;
    }

    public ProductCategory forceDeleteCategory(long id) {
        ProductCategory foundCategory = getCategoryById(id);
        categoryRepository.deleteById(id);
        return foundCategory;
    }
}
