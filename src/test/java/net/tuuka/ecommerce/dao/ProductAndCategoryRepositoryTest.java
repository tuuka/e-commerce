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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import org.springframework.test.context.transaction.TestTransaction;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
// disable using embedded InMemory DB
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIf(value = "${app.test.integration_test_enabled}", loadContext = true)
class ProductAndCategoryRepositoryTest {

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    ProductRepository productRepository;

    List<ProductCategory> categories;
    Product product1, product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(
                "sku1",
                "name1",
                "desc1",
                1.,
                "url1",
                true,
                10);
        product2 = new Product(
                "sku2",
                "name2",
                "desc2",
                2.,
                "url2",
                true,
                20);
        categories = new LinkedList<>(Arrays.asList(
                new ProductCategory("cat1", Arrays.asList(product1, product2)),
                new ProductCategory("cat2")
        ));
        product1.setCategory(categories.get(0));
        product2.setCategory(categories.get(0));
    }

    @AfterEach
    void tearDown() {
        categories = null;
        product1 = product2 = null;
    }

    /* --- Product tests ---*/

    @ParameterizedTest
    // 'sku', 'name' and 'active' fields checking (Dates will assign by Hibernate)
    @ValueSource(strings = {"sku", "name", "active"})
    void givenNullField_whenSave_ShouldThrowException(String fieldName)
            throws NoSuchFieldException, IllegalAccessException {

        // making field equals null with some reflection (should change it)
        Field field = product1.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(product1, null);
        field.setAccessible(false);

        // when save
        // then
        assertThrows(DataIntegrityViolationException.class, () ->
                productRepository.saveAndFlush(product1));

    }

    @Test
    void givenProductWithNullCategory_whenSave_ShouldAssignIdAndSave() {

        // given product1 without category
        product1.setCategory(null);

        // when
        productRepository.saveAndFlush(product1);
        Product fetchedProduct = getProductById(product1.getId());

        // then
        assertEquals(product1, fetchedProduct);

    }

    @Test
    void givenProduct_whenSave_ShouldAssignDates() {

        // given product1

        // when
        productCategoryRepository.saveAndFlush(product1.getCategory());
        productRepository.saveAndFlush(product1);

        // then
        assertNotNull(product1.getCreated());
        assertNotNull(product1.getLastUpdated());

    }

    @Test
    void givenTwoProductsWithSameCategory_whenSave_ShouldAssignIdsAndCascadeSave() {

        // given product1, product2

        // when
        productCategoryRepository.saveAndFlush(product1.getCategory());
        productRepository.saveAndFlush(product1);
        productRepository.saveAndFlush(product2);

        // then
        assertNotNull(product1.getId());
        assertNotNull(product1.getCategory().getId());
        assertNotNull(product2.getCategory().getId());

    }

    @Test
    void givenProductSkuOrName_whenFindAllBySkuLikeAndNameLike_ShouldReturnMatched() {

        // given saved product1, product2, cat_1
        productCategoryRepository.saveAndFlush(product1.getCategory());
        productRepository.saveAndFlush(product1);
        productRepository.saveAndFlush(product2);

        // when
        Page<Product> ame1List = productRepository.findAllBySkuContainsAndNameContains("", "ame1", PageRequest.of(0, 2));
        Page<Product> ku2List = productRepository.findAllBySkuContainsAndNameContains("ku2", "", PageRequest.of(0, 2));

        // then
        assertAll(
                () -> assertNotNull(ame1List),
                () -> assertNotNull(ku2List),
                () -> assertEquals(1, ame1List.getTotalElements()),
                () -> assertEquals(1, ku2List.getTotalElements()),
                () -> assertEquals("sku1", ame1List.getContent().get(0).getSku()),
                () -> assertEquals("name2", ku2List.getContent().get(0).getName())
        );


    }


    /* --- ProductCategory tests ---   */

    @Test
    void givenCategoryName_whenFindByName_ShouldReturnOptional() {

        // given
        String categoryName = "Some fancy name";
        ProductCategory productCategory = new ProductCategory(categoryName);
        productCategoryRepository.saveAndFlush(productCategory);

        // when
        Optional<ProductCategory> fetchedCategory = productCategoryRepository.findByName(categoryName);

        // then
        assertTrue(fetchedCategory.isPresent());
        assertNotNull(fetchedCategory.get().getId());
        assertEquals(categoryName, fetchedCategory.get().getName());
    }

    @Test
    void givenCategoryWithProducts_whenSave_ShouldAssignIdsAndReturnSavedCategory() {

        // given
        ProductCategory categoryToSave = categories.get(0);

        // when
        ProductCategory savedCategory = productCategoryRepository.save(categoryToSave);
        productRepository.saveAll(categoryToSave.getProducts());

        // then
        assertNotNull(savedCategory.getId());
        assertNotNull(savedCategory.getProducts());
        assertNotNull(savedCategory.getProducts().get(0).getId());
    }

    @Test
    void givenCategoryWithProducts_whenDelete_ShouldDeleteCascade() {

        // given
        ProductCategory category = categories.get(0);
        productCategoryRepository.save(category);
        productRepository.saveAll(category.getProducts());

        // when
        productCategoryRepository.deleteById(category.getId());

        // then
        assertFalse(productCategoryRepository
                .findByName(category.getName()).isPresent());

    }

    @Test
    void givenCategoryWithProducts_whenRemoveOneProduct_ShouldUpdate() {

        // given
        ProductCategory category = categories.get(0);
        productCategoryRepository.save(category);
        productRepository.saveAll(category.getProducts());
        commitAndBeginNewTransaction();

        // when
        category = productCategoryRepository.getOne(category.getId());
        Product tempProduct = category.getProducts().get(0);
        tempProduct.setCategory(null);
        productRepository.deleteById(tempProduct.getId());
        commitAndBeginNewTransaction();
        category = productCategoryRepository.getOne(category.getId());

        // then
        assertEquals(1, category.getProducts().size());
        assertEquals(product2.getName(), category.getProducts().get(0).getName());
        productRepository.deleteAll();
        productCategoryRepository.deleteAll();
        commitAndBeginNewTransaction();

    }

    @Test
    void givenCategoryWithProducts_whenChangeCategory_ShouldUpdate() {

        // given
        ProductCategory category = categories.get(0);
        ProductCategory category2 = categories.get(1);
        productCategoryRepository.saveAll(categories);
        productRepository.saveAll(category.getProducts());
        commitAndBeginNewTransaction();

        // when
        category = productCategoryRepository.getOne(category.getId());
        category2 = productCategoryRepository.getOne(category2.getId());
        Product tempProduct = category.getProducts().get(0);
        tempProduct.setCategory(category2);
        commitAndBeginNewTransaction();
        category = productCategoryRepository.getOne(category.getId());
        category2 = productCategoryRepository.getOne(category2.getId());

        // then
        assertEquals(1, category.getProducts().size());
        assertEquals(1, category2.getProducts().size());
        assertEquals(product2.getName(), category.getProducts().get(0).getName());
        assertEquals(product1.getName(), category2.getProducts().get(0).getName());

        productRepository.deleteAll();
        productCategoryRepository.deleteAll();
        commitAndBeginNewTransaction();

    }

    /* ---- helper methods --- */

    private Product getProductById(long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("something wrong"));
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
