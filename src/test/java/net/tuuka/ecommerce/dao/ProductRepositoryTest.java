package net.tuuka.ecommerce.dao;

import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductCategoryRepository productCategoryRepository;

    private Product product;

    @BeforeEach
    void setUp(){
        // setting only NotNull fields
        product = new Product(
                "sku",
                "name",
                null,
                null,
                null,
                false,
                null);
        product.setCategory(new ProductCategory("category1"));
    }

    @AfterEach
    void tearDown(){
        product = null;
    }

    @Test
    void givenNullSku_whenPersist_ShouldThrowException(){
        // given
        product.setSku(null);

        // when
        // then
        assertThrows(SQLException.class, ()->productRepository.save(product));
    }
}
