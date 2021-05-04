package net.tuuka.ecommerce.dao;

import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.util.FakeProductGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

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
@DirtiesContext
@ActiveProfiles("test")
// disable using embedded InMemory DB
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductCategoryRepository productCategoryRepository;

    private Product product;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        products = FakeProductGenerator.getNewFakeProductList(2, 2);
        product = products.get(0);

        // in case of using TestTransaction to control transactions
        if (!TestTransaction.isActive()) TestTransaction.start();
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();
        commitAndBeginNewTransaction();
    }

    @AfterEach
    void tearDown() {
        products = null;
        product = null;
        rollBack();
    }

    @ParameterizedTest
    // 'sku', 'name' and 'active' fields checking (Dates will assign by Hibernate)
    @ValueSource(strings = {"sku", "name", "active"})
    void givenNullField_whenSave_ShouldThrowException(String fieldName)
            throws NoSuchFieldException, IllegalAccessException {

        // making field equals null with some reflection...
        Field field = product.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(product, null);
        field.setAccessible(false);

        // when save
        // then
        assertThrows(DataIntegrityViolationException.class, () -> productRepository.save(product));

    }

    @Test
        // this should never happen because service layer have to check and save categories separately
    void givenProductWithCategory_whenSaveBoth_ShouldSaveBothAnsReturnSavedProduct() {

        // given product with category when save
        // should save 'transient' category first because I won't use Cascade on Product
        productCategoryRepository.save(product.getCategory());
        productRepository.save(product);
        commitAndBeginNewTransaction();

        Product fetchedProduct = getProductById(product.getId());

        // then
        assertNotNull(fetchedProduct);
        assertEquals(product, fetchedProduct);
        assertNotNull(fetchedProduct.getId());
        assertNotNull(fetchedProduct.getCategory().getId());

    }

    @Test
        // as in previous case this should neve happen
    void givenProductWithCategory_whenSaveWithoutCategory_ShouldThrowException() {

        // given product with category that is not persisted
        // when save without saving category
        productRepository.save(product);
        // then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> productRepository.flush());

    }

    @Test
    void givenProductWithoutCategory_whenSave_ShouldAssignIdAndSave() {

        // given product without category
        product.setCategory(null);

        // when
        productRepository.save(product);
        Product fetchedProduct = getProductById(product.getId());

        // then
        assertEquals(product, fetchedProduct);

    }

    @Test
    void givenProduct_whenSave_ShouldAssignDates() {

        // given product without category

        // when
        product.setCategory(null);
        productRepository.save(product);
        productRepository.flush();

        // then
        assertNotNull(product.getCreated());
        assertNotNull(product.getLastUpdated());

    }

    @Test
        // ensure that there is no Cascade at all and won't be in any time may be will delete it later
    void givenProductsWithCategories_whenDoCRUDOperation_ShouldNotTouchPersistedCategories() {

        // given
        List<ProductCategory> persistedCategories = saveAndCommitAllProductsAndCategories(products);

        // when update category name in product
        String previousName = changeCatNameInProductSaveAndCommit(products.get(0));

        // category name must be the same
        Product fetchedProduct = getProductById(products.get(0).getId());
        assertEquals(previousName, fetchedProduct.getCategory().getName());

        // when delete products
        productRepository.deleteAll();
        commitAndBeginNewTransaction();

        // persisted categories should remain untouchable
        assertEquals(0, productRepository.findAll().size());
        assertEquals(persistedCategories, productCategoryRepository.findAll());

    }

    /* ---- helper methods --- */

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

    private List<ProductCategory> saveAndCommitAllProductsAndCategories(List<Product> productList) {
        productCategoryRepository.saveAll(products.stream()
                .map(Product::getCategory).collect(Collectors.toSet()));
        productRepository.saveAll(products);
        commitAndBeginNewTransaction();
        return productCategoryRepository.findAll();
    }

    private String changeCatNameInProductSaveAndCommit(Product tempProduct) {
        String prevName = tempProduct.getCategory().getName();
        tempProduct.getCategory().setName("should not change");
        productRepository.save(tempProduct);
        commitAndBeginNewTransaction();
        return prevName;
    }

    private Product getProductById(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("something wrong"));
    }

}
