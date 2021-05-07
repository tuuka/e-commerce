package net.tuuka.ecommerce.service;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.dao.ProductRepository;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.exception.ProductCategoryNotFoundException;
import net.tuuka.ecommerce.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public Product saveProduct(Product product) {
        Objects.requireNonNull(product);
        if (product.getId() != null)
            throw new IllegalStateException("New Product must have id equals null");
        if (product.getCategory() != null)
            this.validateCategory(product.getCategory());
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(long id) {
        return productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException("Product with id = "
                        + id + " not found"));
    }

    public Product deleteProductById(long id) {
        Product product = this.getProductById(id);
        productRepository.deleteById(id);
        return product;
    }

    public Product updateProduct(Product product) {
        Objects.requireNonNull(product);
        if (product.getId() == null)
            throw new IllegalStateException("Neither updatable product nor product id can be null");

        Product existingProduct = this.getProductById(product.getId());

        // we can save products with null or existed category (but not a new one)
        if (!Objects.equals(existingProduct.getCategory(), product.getCategory())
                && product.getCategory() != null) {
            this.validateCategory(product.getCategory());
        }

        return productRepository.save(product);
    }

    private void validateCategory(ProductCategory category) {
        if (category.getId() != null) {
            categoryShouldExist(category);
            return;
        }
        category.setId(findCategoryByName(category).getId());
    }

    private void categoryShouldExist(ProductCategory givenCategory) {
        long catId = givenCategory.getId();
        ProductCategory existedCategory = productCategoryRepository
                .findById(catId).orElseThrow(() ->
                        new ProductCategoryNotFoundException("Product category with id = "
                                + catId + " not found"));

        if (!existedCategory.getName().equals(givenCategory.getName())) {
            throw new IllegalStateException("Product has a category with id = "
                    + catId + " that already exists but has different name. Existed category name = '"
                    + existedCategory.getName() + " given product Category name = "
                    + givenCategory.getName());
        }
    }

    private ProductCategory findCategoryByName(ProductCategory givenCategory) {
        return productCategoryRepository.findByName(givenCategory.getName())
                .orElseThrow(() -> new ProductCategoryNotFoundException(String.format(
                        "Category with id=%d and name='%s' not found",
                        givenCategory.getId(), givenCategory.getName())));
    }

}
