package net.tuuka.ecommerce.service;

/*
    ProductService should provide at least all CRUD operation on Product repository.
    When saving and updating product must check Category correctness (it have to exist in DB already)
    Mocking repositories here.
*/

import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.dao.ProductRepository;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.util.FakeProductGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest(classes = {ProductService.class, ProductCategoryService.class})
class ProductServiceTest {

    @MockBean
    ProductRepository productRepository;

    @MockBean
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    ProductService productService;

    List<Product> products;
    Product product;

    @BeforeEach
    void setUp() {
        products = FakeProductGenerator.getNewFakeProductList(3, 3);
    }

    @AfterEach
    void tearDown() {
        product = null;
        products = null;
    }

    @Test
    void givenProductWithoutCat_whenSaveProduct_shouldCheckCategoryAndSave() {

        // given product with category
        products.get(0).setCategory(null);
        product = getNewProductLike(products.get(0));
        mockProductFindSaveAndCatFind(product, false, false);

        // when save
        Product savedProduct = productService.save(product);

        // then should get saved product back
        assertEquals(products.get(0), savedProduct);
        assertEquals(1, savedProduct.getId());
        then(productRepository).should().save(eq(product));

    }

    @Test
    void whenGetAllProducts_shouldReturnAllProductList() {

        // given
        FakeProductGenerator.setIdsToGivenProducts(products);
        given(productRepository.findAll()).willReturn(products);

        // when
        List<Product> fetchedProducts = productService.getAll();

        // then
        assertEquals(products, fetchedProducts);
        then(productRepository).should().findAll();

    }


    @Test
    void givenExistingProduct_whenGetProductById_shouldReturnProduct() {

        // given
        products.get(0).setId(1L);
        given(productRepository.findById(anyLong())).willReturn(Optional.of(products.get(0)));

        // when
        Product fetchedProduct = productService.getById(1L);

        // then
        assertEquals(products.get(0), fetchedProduct);
        then(productRepository).should().findById(eq(1L));

    }

    @Test
    void givenNonExistingProductId_whenGetProductById_shouldThrowException() {

        // given
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        // then
        assertThrows(EntityNotFoundException.class, () -> productService.getById(1L));
        then(productRepository).should().findById(eq(1L));

    }

    @Test
    void givenExistingProductId_whenDeleteProductById_shouldReturnDeletedProduct() {

        // given
        products.get(0).setId(1L);
        given(productRepository.findById(anyLong())).willReturn(Optional.of(products.get(0)));
        willDoNothing().given(productRepository).deleteById(anyLong());

        // when
        Product deletedProduct = productService.deleteById(1L);

        // then
        assertEquals(products.get(0), deletedProduct);
        then(productRepository).should().findById(eq(1L));
        then(productRepository).should().deleteById(eq(1L));

    }

    @Test
    void givenNonExistingProductId_whenDeleteProductById_shouldThrowException() {

        // given
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());
        willDoNothing().given(productRepository).deleteById(any());

        // when
        // then
        assertThrows(EntityNotFoundException.class, () -> productService.deleteById(1L));
        then(productRepository).should().findById(eq(1L));
        then(productRepository).should(never()).deleteById(any());

    }

    /*
        'Update tests block' is coming...
        In some of these tests product deep copy is made to distinguish mocked and updatable products
        Assume updateProduct(Product product) method should check for:
            (1) product with id == null
            (2) not existing product (find on id)
        Check for category change. If changed:
            (3) if category didn't change just save product
        Category validation (using in 'saveProduct' method too)
            (4) if category id == null try to find by name and use its id, else throw exception (5)
            (6) if category id != 0 and category doesn't exist (find by id) throw exception
            (7) if category id != 0 and it exists (find by id) but name doesn't match - throw exception
            (8) if category id != 0 and it exists (find by id) simply change and save
    */
    @Test
    // (1) null productId
    void givenNullIdProduct_whenUpdateProduct_shouldThrowException() {

        // given
        given(productRepository.findById(anyLong())).willReturn(Optional.of(products.get(0)));

        // when
        // then
        assertThrows(IllegalStateException.class, () -> productService.update(products.get(0)));
        then(productRepository).should(never()).findById(anyLong());

    }

    @Test
        // (2) not null non existing productID
    void givenNonExistingIdProduct_whenUpdateProduct_shouldThrowException() {

        // given
        products.get(0).setId(1L);
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        // then
        assertThrows(EntityNotFoundException.class, () -> productService.update(products.get(0)));
        then(productRepository).should().findById(eq(1L));

    }

    @Test
        // (3) changed product with same category
    void givenExistingProductWithSameCategory_whenUpdateProduct_shouldUpdateAndReturnUpdated() {
        // given
        Product updatableProduct = products.get(1);
        updatableProduct.setId(existingProduct().getId());

        mockProductFindSaveAndCatFind(updatableProduct, true, true);

        // when
        Product updatedProduct = productService.update(updatableProduct);

        // then
        assertEquals(products.get(1), updatedProduct);
        then(productRepository).should().findById(eq(updatableProduct.getId()));
        then(productCategoryRepository).should(never()).findById(anyLong());
        then(productCategoryRepository).should(never()).findByName(anyString());
        then(productRepository).should().save(eq(updatableProduct));
    }

    @Test
        // (4) same product with new nullId category with existed name
    void givenExistingProductWithNullIdExistingCategory_whenUpdateProduct_shouldSetIdAndReturnProduct() {

        // given
        Product existingProduct = existingProduct();
        Product updatableProduct = getNewProductLike(existingProduct);
        updatableProduct.setCategory(getNullIdCategoryLike(existingProduct.getCategory()));

        mockProductFindSaveAndCatFind(existingProduct, true, false);
        given(productCategoryRepository.findByName(anyString()))
                .willReturn(Optional.of(existingProduct.getCategory()));

        // when
        Product updatedProduct = productService.update(updatableProduct);

        // then
        assertEquals(existingProduct.getCategory().getId(), updatedProduct.getCategory().getId());
        then(productRepository).should().findById(eq(updatableProduct.getId()));
        then(productCategoryRepository).should().findByName(eq(updatableProduct.getCategory().getName()));
        then(productCategoryRepository).should(never()).findById(any());
        then(productRepository).should().save(eq(updatableProduct));

    }

    @Test
        // (5) same product with new nullId category with not existing name
    void givenExistingProductWithNullIdNotExistingCategory_whenUpdateProduct_shouldThrowException() {

        // given
        Product existingProduct = existingProduct();
        Product updatableProduct = getNewProductLike(existingProduct);
        updatableProduct.setCategory(new ProductCategory("not existing"));

        mockProductFindSaveAndCatFind(existingProduct, true, false);
        given(productCategoryRepository.findByName(anyString())).willReturn(Optional.empty());

        // when
        // then
        assertThrows(EntityNotFoundException.class,
                () -> productService.update(updatableProduct));
        then(productRepository).should().findById(eq(updatableProduct.getId()));
        then(productCategoryRepository).should(never()).findById(any());
        then(productCategoryRepository).should().findByName(eq(updatableProduct.getCategory().getName()));
        then(productRepository).should(never()).save(any());

    }

    @Test
        // (6) same product with new not nullId not existed category
    void givenExistedProductWithNotExistingCategory_whenUpdateProduct_shouldThrowException() {

        // given
        Product existingProduct = existingProduct();
        Product updatableProduct = getNewProductLike(existingProduct);
        updatableProduct.setCategory(getNotNullIdCategory());

        mockProductFindSaveAndCatFind(existingProduct, true, false);

        // when
        // then
        assertThrows(EntityNotFoundException.class,
                () -> productService.update(updatableProduct));
        then(productRepository).should().findById(eq(updatableProduct.getId()));
        then(productCategoryRepository).should().findById(eq(updatableProduct.getCategory().getId()));
        then(productRepository).should(never()).save(any());

    }

    @Test
        // (7) same product with new not nullId existed (but with different name) category
    void givenExistingProductWithExistingBadNamedCategory_whenUpdateProduct_shouldThrowException() {

        // given
        Product existingProduct = existingProduct();
        Product updatableProduct = getNewProductLike(existingProduct);
        updatableProduct.setCategory(getNotNullIdCategory());

        mockProductFindSaveAndCatFind(existingProduct, true, true);

        // when
        // then
        assertThrows(IllegalStateException.class,
                () -> productService.update(updatableProduct));
        then(productRepository).should().findById(eq(updatableProduct.getId()));
        then(productCategoryRepository).should().findById(eq(updatableProduct.getCategory().getId()));
        then(productRepository).should(never()).save(any());

    }

    @Test
        // (8) same product with another existing category
    void givenSameProductWithChangedExistingCategory_whenUpdateProduct_shouldUpdateAndReturnUpdated() {

        // given
        Product existingProduct = existingProduct();
        Product updatableProduct = getNewProductLike(existingProduct);
        ProductCategory updatableCategory = getNotNullIdCategory();
        updatableProduct.setCategory(updatableCategory);

        given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(updatableCategory));
        given(productRepository.save(isA(Product.class))).will(
                (InvocationOnMock invocation) -> invocation.getArgument(0)
        );

        // when
        Product updatedProduct = productService.update(updatableProduct);
        // then
        assertEquals(updatableProduct, updatedProduct);
        // just in case of changing in Product equals() method
        assertEquals(updatableProduct.getCategory(), updatedProduct.getCategory());
        then(productRepository).should().findById(eq(updatableProduct.getId()));
        then(productCategoryRepository).should().findById(eq(updatableCategory.getId()));
        then(productRepository).should().save(eq(updatableProduct));

    }

    /* --- helper methods --- */

    private Product getNewProductLike(Product prototype) {
        Product tempProduct = new Product(
                prototype.getSku(),
                prototype.getName(),
                prototype.getDescription(),
                prototype.getUnitPrice(),
                prototype.getImageUrl(),
                prototype.getActive(),
                prototype.getUnitsInStock());
        tempProduct.setId(prototype.getId());
        tempProduct.setCreated(prototype.getCreated());
        tempProduct.setLastUpdated(prototype.getLastUpdated());
        tempProduct.setCategory(prototype.getCategory());
        return tempProduct;
    }

    private ProductCategory getNullIdCategoryLike(ProductCategory category) {
        return new ProductCategory(category.getName());
    }

    private ProductCategory getNotNullIdCategory() {
        ProductCategory category = new ProductCategory("some category");
        category.setId(999L);
        return category;
    }

    private void mockProductFindSaveAndCatFind(Product givenProduct, boolean isProductExist, boolean isCatExist) {
        given(productRepository.findById(anyLong()))
                .willReturn(isProductExist ?
                        Optional.of(givenProduct) :
                        Optional.empty());
        given(productRepository.save(isA(Product.class)))
                .will((InvocationOnMock invocation) -> {
                    Product tempProduct = invocation.getArgument(0);
                    if (tempProduct.getId() == null) tempProduct.setId(1L);
                    return tempProduct;
                });
        given(productCategoryRepository.findById(anyLong()))
                .willReturn(isCatExist ?
                        Optional.of(givenProduct.getCategory()) :
                        Optional.empty());
    }

    private Product existingProduct() {
        FakeProductGenerator.setIdsToGivenProducts(products);
        return products.get(0);
    }

}
