package net.tuuka.ecommerce.service;

import net.tuuka.ecommerce.dao.ProductRepository;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;


@Service
public class ProductService extends BaseCrudAbstractService<Product, Long, ProductRepository> {

    private final ProductCategoryService categoryService;

    public ProductService(ProductRepository repository, ProductCategoryService categoryService) {
        super(repository);
        this.categoryService = categoryService;
    }

    @Transactional
    @Override
    public Product save(Product product) {
        if (product.getCategory() != null) this.validateCategory(product);
        return repository.save(product);
    }

    @Transactional
    @Override
    public Product update(Product product) {
        requireNotNullAndNotNullId(product);

        Product existingProduct = getById(product.getId());
        product.setCreated(existingProduct.getCreated());

        // we can save products with null or persisted category (but not a new one)
        if (!Objects.equals(existingProduct.getCategory(), product.getCategory())
                && product.getCategory() != null) {
            validateCategory(product);
        } else {
            product.setCategory(existingProduct.getCategory());
        }

        return repository.save(product);
    }

    private void validateCategory(Product product) {
        ProductCategory category = product.getCategory();
        ProductCategory existedCategory;

        if (category.getId() != null) {
            existedCategory = categoryService.getById(category.getId());

            if (category.getName() != null && !existedCategory.getName().equals(category.getName())) {
                throw new IllegalStateException(String.format("ProductCategory with " +
                                "id=%d already exists but has different name. " +
                                "Existed category name = '%s' given name = '%s'",
                        existedCategory.getId(), existedCategory.getName(), category.getName()));
            }
        } else {
            existedCategory = categoryService.findByName(category.getName());
        }
        product.setCategory(existedCategory);
    }

    public List<Product> findAllBySkuOrName(String sku, String name) {
        return repository.findAllBySkuContainsAndNameContains(sku, name);
    }
}
