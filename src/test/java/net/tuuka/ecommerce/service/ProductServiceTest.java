package net.tuuka.ecommerce.service;

/*
    ProductService should provide at least all CRUD operation on Product repository.
    When saving and updating product must check Category (it have to exist in DB already)
*/

import net.tuuka.ecommerce.dao.ProductRepository;
import net.tuuka.ecommerce.model.BaseEntity;
import net.tuuka.ecommerce.model.product.Product;
import net.tuuka.ecommerce.model.product.ProductCategory;
import net.tuuka.ecommerce.util.FakeProductGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    ProductCategoryService productCategoryService;

    @InjectMocks
    ProductService productService;

    List<Product> products;
    Product product, existingProduct;
    ProductCategory category, existingCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        products = FakeProductGenerator.getNewFakeProductList(2, 2);
        product = products.get(0);
        category = product.getCategory();
        product.setCategory(null);
        existingCategory = new ProductCategory("existing");
        existingCategory.setId(10L);
        existingProduct = products.get(1);
        existingProduct.setId(10L);
        existingProduct.setCategory(existingCategory);
    }

    @AfterEach
    void tearDown() {
        products = null;
    }

    @Test
    void givenNullArgs_whenDoOperations_shouldThrowNPE() {

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> productService.save(null)),
                () -> assertThrows(NullPointerException.class, () -> productService.findById(null)),
                () -> assertThrows(NullPointerException.class, () -> productService.update(null)),
                () -> assertThrows(NullPointerException.class, () -> productService.deleteById(null))
        );

    }

    @Nested
    @DisplayName("save()")
    class save {

        @Test
        void givenProductWithNotNullId_whenSaveProduct_shouldThrowISE() {

            // given
            product.setId(1L);

            // when
            Executable executable = () -> productService.save(product);

            // then
            assertThrows(IllegalStateException.class, executable);

        }

        @Test
        void givenProductWithExistingSku_whenSaveProduct_shouldThrowISE() {

            // given
            given(productRepository.findBySku(anyString())).willReturn(Optional.of(new Product()));
            given(productRepository.save(isA(Product.class))).willReturn(product);

            // when
            Executable executable = () -> productService.save(product);

            // then
            assertThrows(IllegalStateException.class, executable);
            then(productRepository).should().findBySku(eq(product.getSku()));
            then(productRepository).should(never()).save(eq(product));
        }

        @Test
        void givenProductWithoutCat_whenSaveProduct_shouldAssignIdSaveAndReturn() {

            // given
            // product without category
            given(productRepository.findBySku(anyString())).willReturn(Optional.empty());
            given(productRepository.save(isA(Product.class))).will(ProductServiceTest.this::setIdEquals1AndReturn);

            // when
            Product savedProduct = productService.save(product);

            // then
            assertAll(
                    () -> assertEquals(product.getSku(), savedProduct.getSku()),
                    () -> assertEquals(1, savedProduct.getId())
            );
            then(productRepository).should().findBySku(anyString());
            then(productRepository).should().save(eq(product));

        }

        @Test
        void givenProductWithExistingCat_whenSaveProduct_shouldCheckCategoryAndSave() {

            // given
            category.setId(1L);
            product.setCategory(category);
            given(productCategoryService.findById(anyLong())).willReturn(category);
            given(productRepository.save(isA(Product.class))).will(ProductServiceTest.this::setIdEquals1AndReturn);

            // when
            Product savedProduct = productService.save(product);

            // then
            assertAll(
                    () -> assertEquals(product.getSku(), savedProduct.getSku()),
                    () -> assertEquals(1, savedProduct.getId()),
                    () -> assertEquals(1, savedProduct.getCategory().getId())
            );
            then(productCategoryService).should().findById(anyLong());
            then(productRepository).should().save(eq(product));

        }
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {
        @Test
        void whenGetAllProducts_shouldReturnAllProductList() {

            // given
            given(productRepository.findAll()).willReturn(products);

            // when
            List<Product> fetchedProducts = productService.findAll();

            // then
            assertEquals(products, fetchedProducts);
            then(productRepository).should().findAll();

        }
    }

    @Nested
    @DisplayName("findBuId()")
    class FindById {

        @Test
        void givenExistingProduct_whenFindById_shouldReturnProduct() {

            // given
            product.setId(1L);
            given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

            // when
            Product fetchedProduct = productService.findById(1L);

            // then
            assertEquals(product, fetchedProduct);
            then(productRepository).should().findById(eq(1L));

        }

        @Test
        void givenNonExistingProductId_whenFindById_shouldThrowException() {

            // given
            given(productRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            Executable executable = () -> productService.findById(1L);

            // then
            assertThrows(EntityNotFoundException.class, executable);
            then(productRepository).should().findById(eq(1L));

        }
    }

    @Nested
    @DisplayName("deleteById()")
    class Delete {

        @Test
        void givenExistingProductId_whenDeleteProductById_shouldReturnDeletedProduct() {

            // given
            product.setId(1L);
            given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
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
            Executable executable = () -> productService.deleteById(1L);

            // then
            assertThrows(EntityNotFoundException.class, executable);
            then(productRepository).should().findById(eq(1L));
            then(productRepository).should(never()).deleteById(any());

        }
    }

    @Nested
    @DisplayName("findAllBySkuOrName()")
    class FindAllBySkuOrName {
        @Test
        void givenProductSkuOrName_whenFindAllBySkuOrName_shouldReturnListOfMatched() {

            // given
            given(productRepository.findAllBySkuContainsAndNameContains(anyString(), anyString(), any()))
                    .willReturn(new PageImpl<>(products));

            // when
            Page<Product> fetchedProducts = productService.findAllBySkuOrName("sku", "name", Pageable.unpaged());

            // then
            assertNotNull(fetchedProducts);
            then(productRepository).should().findAllBySkuContainsAndNameContains("sku", "name", Pageable.unpaged());

        }
    }

    /*
        'Update tests block' is coming...
        Here all 'category validate' cases will be checked
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
    @Nested
    @DisplayName("update()")
    class Update {

        @Test
            // (1) null productId
        void givenNullIdProduct_whenUpdateProduct_shouldThrowException() {

            // given
            given(productRepository.save(any())).willReturn(product);

            // when
            Executable executable = () -> productService.update(product);
            // then
            assertThrows(IllegalStateException.class, executable);
            then(productRepository).should(never()).save(any());

        }

        @Test
            // (2) not null non existing productID
        void givenNonExistingIdProduct_whenUpdateProduct_shouldThrowException() {

            // given
            product.setId(1L);
            given(productRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            Executable executable = () -> productService.update(product);

            // then
            assertThrows(EntityNotFoundException.class, executable);
            then(productRepository).should().findById(eq(1L));

        }

        @Test
            // (3) changed product with same category
        void givenExistingProductWithSameCategory_whenUpdateProduct_shouldUpdateAndReturnUpdated() {

            // given
            category.setId(1L);
            product.setId(1L);
            product.setCategory(category);
            existingProduct.setCategory(category);
            given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
            given(productRepository.save(isA(Product.class))).willReturn(product);
            given(productCategoryService.findById(anyLong())).willReturn(null);
            given(productCategoryService.findByName(anyString())).willReturn(null);

            // when
            Product updatedProduct = productService.update(product);

            // then
            assertEquals(product.getSku(), updatedProduct.getSku());
            then(productRepository).should().findById(eq(1L));
            then(productCategoryService).should(never()).findById(anyLong());
            then(productCategoryService).should(never()).findByName(anyString());
            then(productRepository).should().save(eq(product));
        }

        @Test
            // (4) same product with new nullId category with existed name
        void givenExistingProductWithNullIdExistingCategory_whenUpdateProduct_shouldSetIdAndReturnProduct() {

            // given
            existingCategory.setName(category.getName());
            existingProduct.setCategory(existingCategory);
            product.setId(1L);
            product.setCategory(category);
            assertNull(product.getCategory().getId(), "Product category ID must be null here");
            given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
            given(productRepository.save(isA(Product.class))).willReturn(product);
            given(productCategoryService.findById(anyLong())).willReturn(null);
            given(productCategoryService.findByName(anyString())).willReturn(existingCategory);

            // when
            Product updatedProduct = productService.update(product);

            // then
            assertEquals(existingCategory.getId(), updatedProduct.getCategory().getId());
            then(productRepository).should().findById(eq(product.getId()));
            then(productCategoryService).should().findByName(eq(product.getCategory().getName()));
            then(productCategoryService).should(never()).findById(any());
            then(productRepository).should().save(eq(product));

        }

        @Test
            // (5) same product with new nullId category with not existing name
        void givenExistingProductWithNullIdNonExistingCategory_whenUpdateProduct_shouldThrowException() {

            // given
            assertNull(category.getId(), "Product category ID must be null here");
            product.setId(1L);
            product.setCategory(category);

            given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
            given(productRepository.save(isA(Product.class))).willReturn(product);
            given(productCategoryService.findById(anyLong())).willReturn(existingCategory);
            given(productCategoryService.findByName(anyString())).willThrow(EntityNotFoundException.class);

            // when
            Executable executable = () -> productService.update(product);

            // then
            assertThrows(EntityNotFoundException.class, executable);
            then(productRepository).should().findById(eq(product.getId()));
            then(productCategoryService).should(never()).findById(any());
            then(productCategoryService).should().findByName(eq(product.getCategory().getName()));
            then(productRepository).should(never()).save(any());

        }

        @Test
            // (6) same product with new not null Id not existed category
        void givenExistedProductWithNonExistingCategory_whenUpdateProduct_shouldThrowException() {

            // given
            category.setId(1L);
            product.setId(1L);
            product.setCategory(category);

            given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
            given(productRepository.save(isA(Product.class))).willReturn(product);
            given(productCategoryService.findById(anyLong())).willThrow(EntityNotFoundException.class);
            given(productCategoryService.findByName(anyString())).willReturn(existingCategory);

            // when
            Executable executable = () -> productService.update(product);

            // then
            assertThrows(EntityNotFoundException.class, executable);
            then(productRepository).should().findById(eq(product.getId()));
            then(productCategoryService).should().findById(eq(product.getCategory().getId()));
            then(productCategoryService).should(never()).findByName(anyString());
            then(productRepository).should(never()).save(any());

        }

        @Test
            // (7) same product with new not nullId existed (but with different name) category
        void givenExistingProductWithExistingBadNamedCategory_whenUpdateProduct_shouldThrowException() {

            category.setId(1L);
            product.setId(1L);
            product.setCategory(category);

            given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
            given(productRepository.save(isA(Product.class))).willReturn(product);
            given(productCategoryService.findById(anyLong())).willReturn(existingCategory);
            given(productCategoryService.findByName(anyString())).willReturn(null);

            // when
            Executable executable = () -> productService.update(product);

            // then
            assertThrows(IllegalStateException.class, executable);
            then(productRepository).should().findById(eq(product.getId()));
            then(productCategoryService).should().findById(eq(product.getCategory().getId()));
            then(productCategoryService).should(never()).findByName(anyString());
            then(productRepository).should(never()).save(any());

        }

        @Test
            // (8) same product with another existing category with assigned Id
        void givenSameProductWithChangedExistingCategory_whenUpdateProduct_shouldUpdateAndReturnUpdated() {

            // given
            category.setId(1L);
            product.setId(1L);
            product.setCategory(category);

            given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
            given(productRepository.save(isA(Product.class))).willReturn(product);
            given(productCategoryService.findById(anyLong())).willReturn(category);
            given(productCategoryService.findByName(anyString())).willReturn(null);

            // when
            Product updatedProduct = productService.update(product);

            // then
            assertEquals(product, updatedProduct);
            assertEquals(category, updatedProduct.getCategory());
            then(productRepository).should().findById(eq(product.getId()));
            then(productCategoryService).should().findById(product.getCategory().getId());
            then(productRepository).should().save(eq(product));

        }
    }

    /* --- helper methods --- */

    private <T extends BaseEntity> T setIdEquals1AndReturn(InvocationOnMock invocation) {
        T baseEntity = invocation.getArgument(0);
        if (baseEntity.getId() == null) baseEntity.setId(1L);
        return baseEntity;
    }

}
