package net.tuuka.ecommerce.service;

/*
    ProductCategoryService should provide at least all CRUD operation on ProductCategory repository.
    There is a restriction when deleting non empty categories
*/

import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.model.product.Product;
import net.tuuka.ecommerce.model.product.ProductCategory;
import net.tuuka.ecommerce.exception.ProductCategoryNotEmptyException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

class ProductCategoryServiceTest {

    @Mock
    ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    ProductCategoryService categoryService;

    ProductCategory givenCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        givenCategory = new ProductCategory("category");
        givenCategory.setProducts(new ArrayList<>(Collections.singletonList(new Product(
                "sku",
                "name",
                "desc",
                1.,
                "url",
                true,
                99
        ))));
    }

    @AfterEach
    void tearDown() {
        givenCategory = null;
    }

    @Test
    void givenNullArgs_whenDoOperations_shouldThrowNPE() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> categoryService.findById(null)),
                () -> assertThrows(NullPointerException.class, () -> categoryService.deleteById(null)),
                () -> assertThrows(NullPointerException.class, () -> categoryService.update(null)),
                () -> assertThrows(NullPointerException.class, () -> categoryService.forceDeleteCategory(null)),
                () -> assertThrows(NullPointerException.class, () -> categoryService.save(null))
        );

    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        void whenFindAllCategories_shouldReturnCategoryListWithProducts() {

            // given
            List<ProductCategory> categories = Arrays.asList(
                    new ProductCategory("1"),
                    new ProductCategory("2"),
                    new ProductCategory("3")
            );
            categories.get(0).setProducts(givenCategory.getProducts());
            given(productCategoryRepository.findAll()).willReturn(categories);

            // when
            List<ProductCategory> fetchedCategories = categoryService.findAll();

            // then
            assertAll(
                    () -> assertEquals(categories, fetchedCategories),
                    () -> assertEquals(3, fetchedCategories.size()),
                    () -> assertEquals(givenCategory.getProducts().get(0), fetchedCategories.get(0).getProducts().get(0))
            );
            then(productCategoryRepository).should().findAll();
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {
        @Test
        void givenCategoryId_whenFindById_shouldReturnCategoryWithProducts() {

            // given
            givenCategory.setId(1L);
            given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(givenCategory));

            // when
            ProductCategory fetchedCategory = categoryService.findById(1L);

            // then
            assertAll(
                    () -> assertEquals(givenCategory, fetchedCategory),
                    () -> assertEquals(givenCategory.getProducts().get(0), fetchedCategory.getProducts().get(0))
            );
            then(productCategoryRepository).should().findById(eq(1L));

        }

        @Test
        void givenNonExistingCategoryId_whenFindById_shouldThrowException() {

            // given
            given(productCategoryRepository.findById(anyLong())).willReturn(Optional.empty());

            // when
            Executable executable = () -> categoryService.findById(1L);

            // then
            assertThrows(EntityNotFoundException.class, executable);
            then(productCategoryRepository).should().findById(eq(1L));

        }
    }

    @Nested
    @DisplayName("save()")
    class Save {

        @Test
        void givenCategory_whenSaveCategory_shouldAssignIdsAndReturnSavedCategoryWithProducts() {

            // given
            given(productCategoryRepository.save(any())).will(
                    (InvocationOnMock invocation) -> {
                        ProductCategory tempCategory = invocation.getArgument(0);
                        tempCategory.setId(1L);
                        tempCategory.getProducts().get(0).setId(1L);
                        return tempCategory;
                    }
            );

            // when
            ProductCategory savedCategory = categoryService.save(givenCategory);

            // then
            assertAll(
                    () -> assertEquals(givenCategory, savedCategory),
                    () -> assertNotNull(savedCategory.getId()),
                    () -> assertNotNull(savedCategory.getProducts().get(0).getId())
            );
            then(productCategoryRepository).should().save(eq(givenCategory));

        }

        @Test
        void givenNotNullIdCategory_whenSaveCategory_shouldThrowException() {

            // given
            givenCategory.setId(1L);
            given(productCategoryRepository.save(any())).willReturn(givenCategory);

            // when
            Executable executable = () -> categoryService.save(givenCategory);

            // then
            assertThrows(IllegalStateException.class, executable);
            then(productCategoryRepository).should(never()).save(any());

        }
    }

    @Nested
    @DisplayName("update()")
    class Update {
        @Test
        void givenCategory_whenUpdateCategory_shouldReturnUpdatedCategoryWithProducts() {

            // given
            givenCategory.setId(1L);
            given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(givenCategory));
            given(productCategoryRepository.save(any())).willReturn(givenCategory);

            // when
            ProductCategory updatedCategory = categoryService.update(givenCategory);

            // then
            assertAll(
                    () -> assertEquals(givenCategory, updatedCategory),
                    () -> assertEquals(updatedCategory.getProducts().get(0), updatedCategory.getProducts().get(0))
            );
            then(productCategoryRepository).should().findById(eq(1L));
            then(productCategoryRepository).should().save(eq(givenCategory));

        }

        @Test
        void givenNullIdCategory_whenUpdateCategory_shouldThrowException() {

            // given
            given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(givenCategory));
            given(productCategoryRepository.save(any())).willReturn(givenCategory);

            // when
            Executable executable = () -> categoryService.update(givenCategory);

            // then
            assertThrows(IllegalStateException.class, executable);
            then(productCategoryRepository).should(never()).findById(anyLong());
            then(productCategoryRepository).should(never()).save(any());

        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        void givenIdOfCategoryWithEmptyProductList_whenDeleteCategory_shouldReturnDeletedCategory() {

            // given
            givenCategory.getProducts().clear();
            givenCategory.setId(1L);
            given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(givenCategory));
            willDoNothing().given(productCategoryRepository).deleteById(anyLong());

            // when
            ProductCategory deletedCategory = categoryService.deleteById(1L);

            // then
            assertEquals(givenCategory, deletedCategory);
            then(productCategoryRepository).should().findById(1L);
            then(productCategoryRepository).should().deleteById(eq(1L));

        }

        @Test
        void givenIdOfCategoryWithProducts_whenDeleteCategory_shouldThrowException() {

            // given
            givenCategory.setId(1L);
            given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(givenCategory));
            willDoNothing().given(productCategoryRepository).deleteById(anyLong());

            // when
            Executable executable = () -> categoryService.deleteById(1L);

            // then
            assertThrows(ProductCategoryNotEmptyException.class, executable);
            then(productCategoryRepository).should().findById(anyLong());
            then(productCategoryRepository).should(never()).deleteById(eq(1L));

        }

        @Test
        void givenIdOfCategoryWithProducts_whenForceDeleteCategory_shouldReturnDeletedCategory() {

            // given
            givenCategory.setId(1L);
            given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(givenCategory));
            willDoNothing().given(productCategoryRepository).deleteById(anyLong());

            // when
            ProductCategory deletedCategory = categoryService.forceDeleteCategory(1L);

            // then
            assertEquals(givenCategory, deletedCategory);
            then(productCategoryRepository).should().findById(eq(1L));
            then(productCategoryRepository).should().deleteById(eq(1L));

        }
    }

}
