package net.tuuka.ecommerce.service;

import net.tuuka.ecommerce.dao.ProductRepository;
import net.tuuka.ecommerce.model.Product;
import net.tuuka.ecommerce.model.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;


@Service
public class ProductService extends BaseCrudAbstractService<Product, Long, ProductRepository> {

    private final ProductCategoryService categoryService;

    public ProductService(ProductRepository repository, ProductCategoryService categoryService) {
        super(repository);
        this.categoryService = categoryService;
    }

    public Page<Product> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    @Override
    public Product save(Product product) {
        requireNotNullAndNullId(product);
        if (repository.findBySku(product.getSku()).isPresent())
            throw new IllegalStateException(String
                    .format("Product must have unique SKU. " +
                                    "Product with SKU ='%s' already exists",
                            product.getSku()));
        if (product.getCategory() != null) this.validateCategory(product);
        return repository.save(product);
    }

    @Transactional
    @Override
    public Product update(Product product) {
        requireNotNullAndNotNullId(product);

        Product existingProduct = findById(product.getId());
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
            existedCategory = categoryService.findById(category.getId());

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

    public Page<Product> findAllBySkuOrName(String sku, String name, Pageable pageable) {
        return repository.findAllBySkuContainsAndNameContains(sku, name, pageable);
    }

    public Page<Product> findAllByCategory(Long id, Pageable pageable) {
        return repository.findByCategoryId(id, pageable);
    }
}
