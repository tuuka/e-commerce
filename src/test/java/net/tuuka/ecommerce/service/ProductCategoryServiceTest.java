package net.tuuka.ecommerce.service;

/*
    ProductCategoryService should provide at least all CRUD operation on ProductCategory repository.
    There is a restriction when deleting non empty categories
    Mocking repositories here.
*/

import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.exception.ProductCategoryNotEmptyException;
import net.tuuka.ecommerce.exception.ProductCategoryNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@SpringBootTest(classes = {ProductCategoryService.class})
class ProductCategoryServiceTest {

    @MockBean
    ProductCategoryRepository productCategoryRepository;

    @Autowired
    ProductCategoryService categoryService;

    ProductCategory givenCategory;

    @BeforeEach
    void setUp() {
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
    void whenGetAllCategories_shouldReturnCategoryListWithProducts() {

        // given
        List<ProductCategory> categories = Arrays.asList(
                new ProductCategory("1"),
                new ProductCategory("2"),
                new ProductCategory("3")
        );
        categories.get(0).setProducts(givenCategory.getProducts());
        given(productCategoryRepository.findAll()).willReturn(categories);

        // when
        List<ProductCategory> fetchedCategories = categoryService.getAllCategories();

        // then
        assertEquals(categories, fetchedCategories);
        assertEquals(givenCategory.getProducts().get(0), fetchedCategories.get(0).getProducts().get(0));
        then(productCategoryRepository).should().findAll();

    }

    @Test
    void givenCategoryId_whenGetCategoryById_shouldReturnCategoryWithProducts() {

        // given
        givenCategory.setId(1L);
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(givenCategory));

        // when
        ProductCategory fetchedCategory = categoryService.getCategoryById(1L);

        // then
        assertEquals(givenCategory, fetchedCategory);
        assertEquals(givenCategory.getProducts().get(0), fetchedCategory.getProducts().get(0));
        then(productCategoryRepository).should().findById(eq(1L));

    }

    @Test
    void givenNonExistingCategoryId_whenGetCategoryById_shouldThrowException() {

        // given
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        // then
        assertThrows(ProductCategoryNotFoundException.class,
                () -> categoryService.getCategoryById(1L));
        then(productCategoryRepository).should().findById(eq(1L));

    }

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
        ProductCategory savedCategory = categoryService.saveCategory(givenCategory);

        // then
        assertEquals(givenCategory, savedCategory);
        assertNotEquals(null, savedCategory.getId());
        assertNotEquals(null, savedCategory.getProducts().get(0).getId());
        then(productCategoryRepository).should().save(eq(givenCategory));

    }

    @Test
    void givenNotNullIdCategory_whenSaveCategory_shouldThrowException() {

        // given
        givenCategory.setId(1L);
        given(productCategoryRepository.save(any())).willReturn(givenCategory);

        // when
        // then
        assertThrows(IllegalStateException.class, () -> categoryService.saveCategory(givenCategory));
        then(productCategoryRepository).should(never()).save(any());

    }

    @Test
    void givenCategory_whenUpdateCategory_shouldReturnUpdatedCategoryWithProducts() {

        // given
        givenCategory.setId(1L);
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(givenCategory));
        given(productCategoryRepository.save(any())).willReturn(givenCategory);

        // when
        ProductCategory updatedCategory = categoryService.updateCategory(givenCategory);

        // then
        assertEquals(givenCategory, updatedCategory);
        assertEquals(updatedCategory.getProducts().get(0), updatedCategory.getProducts().get(0));
        then(productCategoryRepository).should().findById(eq(1L));
        then(productCategoryRepository).should().save(eq(givenCategory));

    }

    @Test
    void givenNullIdCategory_whenUpdateCategory_shouldThrowException() {

        // given
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(givenCategory));
        given(productCategoryRepository.save(any())).willReturn(givenCategory);

        // when
        // then
        assertThrows(IllegalStateException.class, () -> categoryService.updateCategory(givenCategory));
        then(productCategoryRepository).should(never()).findById(anyLong());
        then(productCategoryRepository).should(never()).save(any());

    }

    @Test
    void givenIdOfCategoryWithEmptyProductList_whenDeleteCategory_shouldReturnDeletedCategory() {

        // given
        givenCategory.getProducts().clear();
        givenCategory.setId(1L);
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(givenCategory));
        willDoNothing().given(productCategoryRepository).deleteById(anyLong());

        // when
        ProductCategory deletedCategory = categoryService.deleteCategory(1L);

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
        // then
        assertThrows(ProductCategoryNotEmptyException.class, () ->
                categoryService.deleteCategory(1L));
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

    @Test
    void givenNullArg_whenAnyMethodInvoked_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> categoryService.saveCategory(null));
        assertThrows(NullPointerException.class, () -> categoryService.updateCategory(null));
    }

}
