package net.tuuka.ecommerce.dao;

import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
    Attempts to save Product with fields equal null that can't be null must throw exception


    Assume Product class has following not null fields:
        private String sku;                 // not null
        private String name;                // not null
        private Boolean active;             // not null

    not checked these fields as Hibernate assign them automatically because of @CreationTimestamp &
        private ZonedDateTime created;      // not null
        private ZonedDateTime lastUpdated;  // not null

    And Category class has not null field:
        private String name;                // not null
*/

// TODO: test time conversation between different time zones

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductCategoryRepository productCategoryRepository;

    private Product product;

    @BeforeEach
    void setUp() {
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
    void tearDown() {
        product = null;
    }

//    @ParameterizedTest
//    @MethodSource("provideProducts")
//    void givenNullSku_whenSave_ShouldThrowException(Product testProduct) {
//        assertThrows(DataIntegrityViolationException.class, () -> productRepository.save(testProduct));
//    }
//    private static Stream<Product> provideProducts() {
//        return Stream.of(
//                new Product(null, "name", null, null, null, false, null),
//                new Product("sku", null, null, null, null, false, null),
//                new Product("sku", "name", null, null, null, null, null)
//        );
//    }

    @ParameterizedTest
    // 'sku', 'name' and 'active' fields checking (Dates will assign by Hibernate)
    @ValueSource(strings = {"sku", "name", "active"})
    void givenNullField_whenSave_ShouldThrowException(String fieldName) throws NoSuchFieldException, IllegalAccessException {
        // given field equals null with some reflection...
        Field field = product.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(product, null);
        field.setAccessible(false);

        // when save
        // then
        assertThrows(DataIntegrityViolationException.class, () -> productRepository.save(product));
    }

    @Test
    void givenProduct_whenSave_ShouldSave() {
        // given product with category
        // when save 'transient' category first
        productCategoryRepository.save(product.getCategory());
        productRepository.save(product);
        productRepository.flush();
        Product fetchedProduct = productRepository.findById(product.getId())
                .orElse(null);
        // then
        assertEquals(product, fetchedProduct);
    }

    @Test
    void givenProductWithCategory_whenSaveWithoutCategory_ShouldThrowException() {
        // given product with category
        // when save without saving category
        productRepository.save(product);
        // then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> productRepository.flush());
    }

}
