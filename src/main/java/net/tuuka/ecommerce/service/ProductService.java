package net.tuuka.ecommerce.service;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.dao.ProductRepository;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.exception.ProductCategoryNotFoundException;
import net.tuuka.ecommerce.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    public Product saveProduct(Product product) {
        Objects.requireNonNull(product);
        if (product.getId() != null)
            throw new IllegalStateException("New Product must have id equals null");
        if (product.getCategory() != null)
            this.validateCategory(product);
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

    @Transactional
    public Product updateProduct(Product product) {
        Objects.requireNonNull(product);
        if (product.getId() == null)
            throw new IllegalStateException("Product id can't be null");

        Product existingProduct = this.getProductById(product.getId());
        product.setCreated(existingProduct.getCreated());

        // we can save products with null or persisted category (but not a new one)
        if (!Objects.equals(existingProduct.getCategory(), product.getCategory())
                && product.getCategory() != null) {
            this.validateCategory(product);
        } else {
            product.setCategory(existingProduct.getCategory());
        }

        return productRepository.save(product);
    }

    private void validateCategory(Product product) {
        ProductCategory category = product.getCategory();
        ProductCategory existedCategory;
        if (category.getId() != null) {
            long catId = category.getId();
            existedCategory = productCategoryRepository
                    .findById(catId).orElseThrow(() ->
                            new ProductCategoryNotFoundException("Product category with id = "
                                    + catId + " not found"));

            if (category.getName() != null && !existedCategory.getName().equals(category.getName())) {
                throw new IllegalStateException(String.format("ProductCategory with " +
                                "id=%d already exists but has different name. " +
                                "Existed category name = '%s' given name = '%s'",
                        catId, existedCategory.getName(), category.getName()));
            }
        } else {
            existedCategory = findCategoryByName(category);
        }
        product.setCategory(existedCategory);
    }

    private ProductCategory findCategoryByName(ProductCategory givenCategory) {
        return productCategoryRepository.findByName(givenCategory.getName())
                .orElseThrow(() -> new ProductCategoryNotFoundException(String.format(
                        "Category with name='%s' not found",
                        givenCategory.getName())));
    }

}
