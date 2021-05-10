package net.tuuka.ecommerce.service;

/*
    ProductService should provide at least all CRUD operation on Product repository.
    When saving and updating product must check Category correctness (it have to exist in DB already)
    Mocking repositories here.
*/

import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.dao.ProductRepository;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.util.FakeProductGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ProductServiceIntegrationTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    ProductService productService;

    List<Product> products;
    Product product1, product2, product3;

    @BeforeEach
    void setUp() {
        products = FakeProductGenerator.getNewFakeProductList(3, 3);
        product1 = products.get(0);
        product2 = products.get(1);
        product3 = products.get(2);
    }

    @AfterEach
    void tearDown() {
        product1 = product2 = product3 = null;
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();
    }

    @Test
    void givenProductWithoutCat_whenSaveProduct_shouldSave() {

        // given product with category
        product1.setCategory(null);

        // when save
        Product savedProduct = productService.save(product1);
        Product fetchedProduct = productService.getById(savedProduct.getId());

        // then should get saved product back
        assertEquals(savedProduct, fetchedProduct);
        assertNull(fetchedProduct.getCategory());

    }

    @Test
    void givenProductWithCatWithNullId_whenSaveProduct_shouldSave() {

        assertThrows(EntityNotFoundException.class, () ->
                productService.save(product1));

    }


    @Test
    void givenNonExistingProductId_whenGetProductById_shouldThrowException() {

        // given
        long id = 999L;
        // when
        // then
        assertThrows(EntityNotFoundException.class, () -> productService.getById(id));

    }


    private void commitAndBeginNewTransaction() {
        if (TestTransaction.isActive()) {
            TestTransaction.flagForCommit();
            TestTransaction.end();
        }
        TestTransaction.start();
    }

    private void rollBack() {
        if (TestTransaction.isActive()) {
            TestTransaction.flagForRollback();
            TestTransaction.end();
        }
    }

}
