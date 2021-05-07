package net.tuuka.ecommerce.service;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.entity.ProductCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;

    public List<ProductCategory> getAllCategories(){
        return categoryRepository.findAll();
    }
}
