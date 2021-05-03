package net.tuuka.ecommerce.service;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.dao.ProductRepository;
import net.tuuka.ecommerce.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    public Product save(Product product) {
        productCategoryRepository.save(product.getCategory());
        return productRepository.save(product);
    }
}
