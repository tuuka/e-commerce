package net.tuuka.ecommerce.service;

import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.exception.ProductCategoryNotEmptyException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
public class ProductCategoryService extends
        BaseCrudAbstractService<ProductCategory, Long, ProductCategoryRepository> {

    public ProductCategoryService(ProductCategoryRepository repository) {
        super(repository);
    }

    @Override
    public ProductCategory deleteById(Long id) {
        requireNonNull(id);
        ProductCategory foundCategory = getById(id);
        if (foundCategory.getProducts().size() > 0)
            throw new ProductCategoryNotEmptyException(String.format(
                    "Category with id=%d and name='%s' is not empty",
                    id, foundCategory.getName()));
        repository.deleteById(id);
        return foundCategory;
    }

    @Transactional
    public ProductCategory forceDeleteCategory(Long id) {
        requireNonNull(id);
        ProductCategory foundCategory = getById(id);
        repository.deleteById(id);
        return foundCategory;
    }

    public ProductCategory findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Category with name='%s' not found", name)));
    }

}
