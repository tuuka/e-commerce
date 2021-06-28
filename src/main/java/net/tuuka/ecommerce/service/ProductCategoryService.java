package net.tuuka.ecommerce.service;

import net.tuuka.ecommerce.controller.dto.CategoryRepresentation;
import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.model.ProductCategory;
import net.tuuka.ecommerce.exception.ProductCategoryNotEmptyException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductCategoryService extends
        BaseCrudAbstractService<ProductCategory, Long, ProductCategoryRepository> {

    public ProductCategoryService(ProductCategoryRepository repository) {
        super(repository);
    }

    @Override
    public ProductCategory update(ProductCategory category) {
        requireNotNullAndNotNullId(category);
        category.setProducts(findById(category.getId()).getProducts());
        return repository.save(category);
    }

    @Override
    public ProductCategory deleteById(Long id) {
        requireNonNull(id);
        ProductCategory foundCategory = findById(id);
        if (foundCategory.getProducts().size() > 0)
            throw new ProductCategoryNotEmptyException(String.format(
                    "Category with id=%d and name='%s' is not empty",
                    id, foundCategory.getName()));
        repository.deleteById(id);
        return foundCategory;
    }

    public ProductCategory forceDeleteCategory(Long id) {
        requireNonNull(id);
        ProductCategory foundCategory = findById(id);
        repository.deleteById(id);
        return foundCategory;
    }

    public ProductCategory findByName(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Category with name='%s' not found", name)));
    }

    public List<CategoryRepresentation> findAllWithoutProducts() {
        return repository
                .findAll().stream().map(CategoryRepresentation::new)
                .collect(Collectors.toList());
    }
}
