package net.tuuka.ecommerce.dao;

import net.tuuka.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllBySkuContainsAndNameContains(String sku, String name);

    Optional<Product> findBySku(String sku);

}
