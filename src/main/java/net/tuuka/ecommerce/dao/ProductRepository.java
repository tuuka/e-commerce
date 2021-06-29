package net.tuuka.ecommerce.dao;

import net.tuuka.ecommerce.model.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findAllBySkuContainsAndNameContains(String sku, String name, Pageable pageable);

    Page<Product> findByCategoryId(Long id, Pageable pageable);

    Optional<Product> findBySku(String sku);

}
