package net.tuuka.ecommerce.dao;

import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.util.FakeProductGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
    Attempts to save Product with fields equal null that can't be null must throw exception

    Assume Product class has following not null fields:
        private String sku;                 // not null
        private String name;                // not null
        private Boolean active;             // not null

    Hibernate assign these fields automatically because of @CreationTimestamp and @UpdateTimestamp
        private ZonedDateTime created;      // not null
        private ZonedDateTime lastUpdated;  // not null

    And Category class has not null field:
        private String name;                // not null
*/

// TODO: test time conversation between different time zones

@DataJpaTest
@ActiveProfiles("test")
// disable using embedded InMemory DB
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@DirtiesContext
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    private Product product;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        products = FakeProductGenerator.getNewFakeProductList(2, 2);
        product = products.get(0);
    }

    @AfterEach
    void tearDown() {
        products = null;
        product = null;
    }

    @ParameterizedTest
    // 'sku', 'name' and 'active' fields checking (Dates will assign by Hibernate)
    @ValueSource(strings = {"sku", "name", "active"})
    void givenNullField_whenSave_ShouldThrowException(String fieldName)
            throws NoSuchFieldException, IllegalAccessException {

        // making field equals null with some reflection (should change it)
        Field field = product.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(product, null);
        field.setAccessible(false);

        // when save
        // then
        assertThrows(DataIntegrityViolationException.class, () ->
                productRepository.saveAndFlush(product));

    }


    @Test
    void givenProductWithoutCategory_whenSave_ShouldAssignIdAndSave() {

        // given product without category
        product.setCategory(null);

        // when
        productRepository.saveAndFlush(product);
        Product fetchedProduct = getProductById(product.getId());

        // then
        assertEquals(product, fetchedProduct);

    }

    @Test
    void givenProduct_whenSave_ShouldAssignDates() {

        // given product

        // when
        productRepository.saveAndFlush(product);

        // then
        assertNotNull(product.getCreated());
        assertNotNull(product.getLastUpdated());

    }

    /* ---- helper methods --- */

    private Product getProductById(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("something wrong"));
    }

}
