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

        if (product == null || product.getId() == null)
            throw new IllegalStateException("Neither product nor product id can be null");

        Product existingProduct = this.getProductById(product.getId());

        // check for product category difference and possible nulls
        if (!Objects.equals(existingProduct.getCategory(), product.getCategory())
                && product.getCategory() != null) {
            this.validateCategory(product.getCategory());
        }

        return productRepository.save(product);
    }

    // check if given ProductCategory persisted and has correct name
    private void validateCategory(ProductCategory category) {
        ProductCategory existedCategory;
        if (category.getId() != null) {
            long catId = category.getId();
            existedCategory = productCategoryRepository
                    .findById(catId).orElseThrow(() ->
                            new ProductCategoryNotFoundException("Product category with id = "
                                    + catId + " not found"));
            if (!existedCategory.getName().equals(category.getName())) {
                throw new IllegalStateException("Can't save Product with Category id = "
                        + catId + " because of name inconsistency. Persisted Category name = '"
                        + existedCategory.getName() + " when updating product Category name = "
                        + category.getName());
            }
        } else {
            String catName = category.getName();
            existedCategory = productCategoryRepository.findByName(catName)
                    .orElseThrow(() ->
                            new ProductCategoryNotFoundException("Product category with name = "
                                    + catName + " not found"));
            category.setId(existedCategory.getId());
        }
    }

}
