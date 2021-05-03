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
import net.tuuka.ecommerce.exception.ProductCategoryNotFoundException;
import net.tuuka.ecommerce.exception.ProductNotFoundException;
import net.tuuka.ecommerce.util.FakeProductGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest(classes = {ProductService.class})
class ProductServiceTest {

    @MockBean
    ProductRepository productRepository;

    @MockBean
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    ProductService productService;

    List<Product> products;
    List<ProductCategory> productCategories;

    @BeforeEach
    void setUp() {
        // using predefined list of product with categories with null ids
        // make a deep copy of generated values
        products = FakeProductGenerator.getFakeProductList()
                .stream()
                .map(p -> {
                    Product newProduct = new Product(
                            p.getSku(),
                            p.getName(),
                            p.getDescription(),
                            p.getUnitPrice(),
                            p.getImageUrl(),
                            p.getActive(),
                            p.getUnitsInStock());
                    newProduct.setCategory(new ProductCategory(p.getCategory().getName()));
                    return newProduct;
                })
                .collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {
        products = null;
        productCategories = null;
    }

    // validate Category later in 'update block'
    @Test
    void givenProductWithCat_whenSaveProduct_shouldCheckCategoryAndSave() {

        // given product with category
        Product product = products.get(0);
        ProductCategory productCategory = product.getCategory();
        productCategory.setId(1L);
        given(productRepository.save(isA(Product.class)))
                .will((InvocationOnMock invocation) -> {
                    Product tempProduct = invocation.getArgument(0);
                    tempProduct.setId(1L);
                    return tempProduct;
                });
        given(productCategoryRepository.findById(anyLong()))
                .willReturn(Optional.of(productCategory));

        // when save
        Product savedProduct = productService.saveProduct(product);

        // then should get saved product back
        assertEquals(product, savedProduct);
        assertEquals(1, savedProduct.getId());
        assertEquals(1, savedProduct.getCategory().getId());
        then(productRepository).should().save(eq(product));
        then(productCategoryRepository).should().findById(eq(1L));

    }

    @Test
    void whenGetAllProducts_shouldReturnAllProductList() {

        // given
        this.setIds(products);
        given(productRepository.findAll()).willReturn(products);

        // when
        List<Product> fetchedProducts = productService.getAllProducts();

        // then
        assertEquals(products, fetchedProducts);
        then(productRepository).should().findAll();

    }


    @Test
    void givenExistingProductId_whenGetProductById_shouldReturnProduct() {

        // given
        given(productRepository.findById(anyLong())).willReturn(Optional.of(products.get(0)));

        // when
        Product fetchedProduct = productService.getProductById(1L);

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
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
        then(productRepository).should().findById(eq(1L));

    }

    @Test
    void givenExistingProductId_whenDeleteProductById_shouldReturnDeletedProduct() {

        // given
        products.get(0).setId(1L);
        long id = products.get(0).getId();
        given(productRepository.findById(anyLong())).willReturn(Optional.of(products.get(0)));
        willDoNothing().given(productRepository).deleteById(anyLong());

        // when
        Product deletedProduct = productService.deleteProductById(id);

        // then
        assertEquals(products.get(0), deletedProduct);
        then(productRepository).should().findById(eq(id));
        then(productRepository).should().deleteById(eq(id));

    }

    @Test
    void givenNonExistingProductId_whenDeleteProductById_shouldThrowException() {

        // given
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());
        willDoNothing().given(productRepository).deleteById(any());

        // when
        // then
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProductById(1L));
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
    void givenProductWithNullId_whenUpdateProduct_shouldThrowException() {

        // given
        given(productRepository.findById(anyLong()))
                .willReturn(Optional.of(products.get(0)));

        // when
        // then
        assertThrows(IllegalStateException.class, () ->
                productService.updateProduct(products.get(0)));
        then(productRepository).should(never()).findById(anyLong());

    }

    @Test
        // (2) not null non existing productID
    void givenProductWithNonExistingId_whenUpdateProduct_shouldThrowException() {

        // given
        products.get(0).setId(1L);
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        // then
        assertThrows(ProductNotFoundException.class, () ->
                productService.updateProduct(products.get(0)));
        then(productRepository).should().findById(eq(1L));

    }

    @Test
        // (3) changed product with same category
    void givenChangedProductWithSameCategory_whenUpdateProduct_shouldUpdateAndReturnUpdated() {
        // given
        this.setIds(products);
        Product existingProduct = products.get(0);
        Product updatableProduct = new Product(
                "new sku",
                "new name",
                "new desc",
                0.0,
                "new url",
                false,
                0);
        updatableProduct.setId(existingProduct.getId());
        ProductCategory sameCategory = existingProduct.getCategory();
        sameCategory.setId(1L);
        updatableProduct.setCategory(sameCategory);
        given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(sameCategory));
        given(productRepository.save(isA(Product.class))).will(
                (InvocationOnMock invocation) -> invocation.getArgument(0)
        );

        // when
        Product updatedProduct = productService.updateProduct(updatableProduct);

        // then
        assertEquals(updatableProduct, updatedProduct);
        then(productRepository).should().findById(eq(updatableProduct.getId()));
        then(productCategoryRepository).should(never()).findById(anyLong());
        then(productRepository).should().save(eq(updatableProduct));
    }

    @Test
        // (4) same product with new nullId category with existed name
    void givenSameProductWithChangedNullIdExistingCategory_whenUpdateProduct_shouldUpdateAndReturnUpdated() {

        // given
        Product existingProduct = products.get(0);
        existingProduct.setId(1L);
        Product updatableProduct = getNewProductLike(existingProduct);
        ProductCategory updatableCategory = new ProductCategory("some existed name");
        updatableProduct.setCategory(updatableCategory);
        ProductCategory existingCategory = new ProductCategory("some existed name");
        existingCategory.setId(5L);

        given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
        given(productCategoryRepository.findByName(anyString()))
                .willReturn(Optional.of(existingCategory));
        given(productRepository.save(isA(Product.class))).will(
                (InvocationOnMock invocation) -> invocation.getArgument(0)
        );

        // when
        Product updatedProduct = productService.updateProduct(updatableProduct);

        // then
        assertEquals(existingCategory.getId(), updatedProduct.getCategory().getId());
        then(productRepository).should().findById(eq(updatableProduct.getId()));
        then(productCategoryRepository).should().findByName(eq(updatableCategory.getName()));
        then(productRepository).should().save(eq(updatableProduct));

    }

    @Test
        // (5) same product with new nullId category with not existed name
    void givenSameProductWithChangedNullIdNotExistingCategory_whenUpdateProduct_shouldThrowException() {

        // given
        Product existingProduct = products.get(0);
        existingProduct.setId(1L);
        Product updatableProduct = getNewProductLike(existingProduct);
        ProductCategory updatableCategory = new ProductCategory("some not existed name");
        updatableProduct.setCategory(updatableCategory);

        given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
        given(productCategoryRepository.findByName(anyString())).willReturn(Optional.empty());
        given(productRepository.save(isA(Product.class))).will(
                (InvocationOnMock invocation) -> invocation.getArgument(0)
        );

        // when
        // then
        assertThrows(ProductCategoryNotFoundException.class,
                () -> productService.updateProduct(updatableProduct));
        then(productRepository).should().findById(eq(updatableProduct.getId()));
        then(productCategoryRepository).should().findByName(eq(updatableCategory.getName()));
        then(productRepository).should(never()).save(any());

    }

    @Test
        // (6) same product with new not nullId not existed category
    void givenSameProductWithChangedNotExistingCategory_whenUpdateProduct_shouldThrowException() {

        // given
        Product existingProduct = products.get(0);
        existingProduct.setId(1L);
        Product updatableProduct = getNewProductLike(existingProduct);
        ProductCategory updatableCategory = new ProductCategory("some not existed category");
        updatableCategory.setId(5L);
        updatableProduct.setCategory(updatableCategory);

        given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.empty());
        given(productRepository.save(isA(Product.class))).will(
                (InvocationOnMock invocation) -> invocation.getArgument(0)
        );

        // when
        // then
        assertThrows(ProductCategoryNotFoundException.class,
                () -> productService.updateProduct(updatableProduct));
        then(productRepository).should().findById(eq(updatableProduct.getId()));
        then(productCategoryRepository).should().findById(eq(updatableCategory.getId()));
        then(productRepository).should(never()).save(any());

    }

    @Test
        // (7) same product with new not nullId existed (but with different name) category
    void givenSameProductWithChangedExistingBadNamedCategory_whenUpdateProduct_shouldThrowException() {

        // given
        Product existingProduct = products.get(0);
        existingProduct.setId(1L);
        Product updatableProduct = getNewProductLike(existingProduct);
        ProductCategory updatableCategory = new ProductCategory("some category");
        updatableCategory.setId(5L);
        updatableProduct.setCategory(updatableCategory);
        ProductCategory existingCategory = new ProductCategory("some existed name");
        existingCategory.setId(5L);

        given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(existingCategory));
        given(productRepository.save(isA(Product.class))).will(
                (InvocationOnMock invocation) -> invocation.getArgument(0)
        );

        // when
        // then
        assertThrows(IllegalStateException.class,
                () -> productService.updateProduct(updatableProduct));
        then(productRepository).should().findById(eq(updatableProduct.getId()));
        then(productCategoryRepository).should().findById(eq(updatableCategory.getId()));
        then(productRepository).should(never()).save(any());

    }

    @Test
        // (8) same product with another existing category
    void givenSameProductWithChangedExistingCategory_whenUpdateProduct_shouldUpdateAndReturnUpdated() {

        // given
        Product existingProduct = products.get(0);
        existingProduct.setId(1L);
        Product updatableProduct = getNewProductLike(existingProduct);
        ProductCategory updatableCategory = new ProductCategory("some existed category");
        updatableCategory.setId(5L);
        updatableProduct.setCategory(updatableCategory);

        given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(updatableCategory));
        given(productRepository.save(isA(Product.class))).will(
                (InvocationOnMock invocation) -> invocation.getArgument(0)
        );

        // when
        Product updatedProduct = productService.updateProduct(updatableProduct);
        // then
        assertEquals(updatableProduct, updatedProduct);
        // just in case of changing in Product equals() method
        assertEquals(updatableProduct.getCategory(), updatedProduct.getCategory());
        then(productRepository).should().findById(eq(updatableProduct.getId()));
        then(productCategoryRepository).should().findById(eq(updatableCategory.getId()));
        then(productRepository).should().save(eq(updatableProduct));

    }

    // Repositories assign ids automatically. As we mocking repos in
    // this test class we may need to assign ids manually
    private void setIds(List<Product> productList) {
        IntStream.range(0, productList.size()).forEach(i ->
        {
            productList.get(i).setId((long) i + 1);
            ProductCategory cat = productList.get(i).getCategory();
            cat.setId(Long.parseLong(cat.getName().split("_")[1]) + 1);
        });
    }

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

}
