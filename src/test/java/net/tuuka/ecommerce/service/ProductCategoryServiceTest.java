package net.tuuka.ecommerce.service;

/*
    ProductCategoryService should provide at least all CRUD operation on ProductCategory repository.
    Mocking repositories here.
*/

import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.exception.ProductCategoryNotFoundException;
import net.tuuka.ecommerce.util.FakeProductGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.Invocation;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    List<Product> products;
    List<ProductCategory> categories;

    @BeforeEach
    void setUp() {
        products = FakeProductGenerator.getNewFakeProductList(3, 3);
        categories = products.stream().map(Product::getCategory).distinct().collect(Collectors.toList());
    }

    @AfterEach
    void tearDown() {
        products = null;
        categories = null;
    }

    @Test
    void whenGetAllCategories_shouldReturnCategoryListWithProducts() {

        // given
        given(productCategoryRepository.findAll()).willReturn(categories);

        // when
        List<ProductCategory> fetchedCategories = categoryService.getAllCategories();

        // then
        assertEquals(categories, fetchedCategories);
        assertEquals(categories.get(0).getProducts(), fetchedCategories.get(0).getProducts());
        then(productCategoryRepository).should().findAll();

    }

    @Test
    void givenCategoryId_whenGetCategoryById_shouldReturnCategoryWithProducts() {

        // given
        FakeProductGenerator.setIdsToGivenProducts(products);
        ProductCategory existingCategory = categories.get(0);
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(existingCategory));

        // when
        ProductCategory fetchedCategory = categoryService.getCategoryById(existingCategory.getId());

        // then
        assertEquals(existingCategory, fetchedCategory);
        assertEquals(existingCategory.getProducts(), fetchedCategory.getProducts());
        then(productCategoryRepository).should().findById(eq(existingCategory.getId()));

    }

    @Test
    void givenNonExistingCategoryId_whenGetCategoryById_shouldThrowException() {

        // given
        ProductCategory existingCategory = categories.get(0);
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        // then
        assertThrows(ProductCategoryNotFoundException.class,
                ()->categoryService.getCategoryById(1L));
        then(productCategoryRepository).should().findById(eq(1L));

    }

    @Test
    void givenCategory_whenSaveCategory_shouldAssignIdsAndReturnSavedCategoryWithProducts() {

        // given
        ProductCategory categoryToSave = categories.get(0);
        given(productCategoryRepository.save(any())).will(
                (InvocationOnMock invocation) -> {
                    ProductCategory tempCategory = invocation.getArgument(0);
                    assignIdsToCategory(tempCategory);
                    return tempCategory;
                }
        );

        // when
        ProductCategory savedCategory = categoryService.saveCategory(categoryToSave);

        // then
        assertEquals(categoryToSave, savedCategory);
        assertNotEquals(null, savedCategory.getId());
        assertNotEquals(null, savedCategory.getProducts().get(0).getId());
        then(productCategoryRepository).should().save(eq(categoryToSave));

    }

    @Test
    void givenNotNullIdCategory_whenSaveCategory_shouldThrowException() {

        // given
        categories.get(0).setId(1L);
        given(productCategoryRepository.save(any())).willReturn(categories.get(0));

        // when
        // then
        assertThrows(IllegalStateException.class, ()-> categoryService.saveCategory(categories.get(0)));
        then(productCategoryRepository).should(never()).save(any());

    }

    @Test
    void givenCategory_whenUpdateCategory_shouldReturnUpdatedCategoryWithProducts() {

        // given
        ProductCategory categoryToUpdate = categories.get(0);
        assignIdsToCategory(categoryToUpdate);
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(categoryToUpdate));
        given(productCategoryRepository.save(any())).willReturn(categoryToUpdate);

        // when
        ProductCategory updatedCategory = categoryService.updateCategory(categoryToUpdate);

        // then
        assertEquals(categoryToUpdate, updatedCategory);
        assertEquals(updatedCategory.getProducts(), updatedCategory.getProducts());
        then(productCategoryRepository).should().findById(eq(categoryToUpdate.getId()));
        then(productCategoryRepository).should().save(eq(categoryToUpdate));

    }

    @Test
    void givenNullIdCategory_whenUpdateCategory_shouldThrowException() {

        // given
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(categories.get(0)));
        given(productCategoryRepository.save(any())).willReturn(categories.get(0));

        // when
        // then
        assertThrows(IllegalStateException.class, ()-> categoryService.updateCategory(categories.get(0)));
        then(productCategoryRepository).should(never()).findById(anyLong());
        then(productCategoryRepository).should(never()).save(any());

    }

    @Test
    void givenIdOfCategoryWithEmptyProductList_whenDeleteCategory_shouldReturnDeletedCategory() {

        // given
        ProductCategory categoryToDelete = categories.get(0);
        categoryToDelete.getProducts().clear();
        categoryToDelete.setId(1L);
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(categoryToDelete));
        willDoNothing().given(productCategoryRepository).deleteById(anyLong());

        // when
        ProductCategory deletedCategory = categoryService.deleteCategory(categoryToDelete.getId());

        // then
        assertEquals(categoryToDelete, deletedCategory);
        then(productCategoryRepository).should().findById(anyLong());
        then(productCategoryRepository).should().deleteById(eq(1L));

    }

    @Test
    void givenIdOfCategoryWithProducts_whenDeleteCategory_shouldThrowException() {

        // given
        ProductCategory categoryToDelete = categories.get(0);
        categoryToDelete.setId(1L);
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(categoryToDelete));
        willDoNothing().given(productCategoryRepository).deleteById(anyLong());

        // when
        // then
        assertThrows(ProductCategoryNotEmptyException.class,
                categoryService.deleteCategory(categoryToDelete.getId()));
        then(productCategoryRepository).should().findById(anyLong());
        then(productCategoryRepository).should(never()).deleteById(eq(1L));

    }

    @Test
    void givenIdOfCategoryWithProducts_whenForceDeleteCategory_shouldReturnDeletedCategory() {

        // given
        ProductCategory categoryToDelete = categories.get(0);
        categoryToDelete.setId(1L);
        given(productCategoryRepository.findById(anyLong())).willReturn(Optional.of(categoryToDelete));
        willDoNothing().given(productCategoryRepository).deleteById(anyLong());

        // when
        ProductCategory deletedCategory = categoryService.forceDeleteCategory(1L);

        // then
        assertEquals(categoryToDelete, deletedCategory);
        then(productCategoryRepository).should().findById(eq(categoryToDelete.getId()));
        then(productCategoryRepository).should().deleteById(eq(1L));

    }

    @Test
    void givenNullArg_whenAnyMethodInvoked_shouldThrowException(){
        assertThrows(NullPointerException.class, categoryService.saveCategory(null));
        assertThrows(NullPointerException.class, categoryService.updateCategory(null));
    }

    private void assignIdsToCategory(ProductCategory productCategory){
        productCategory.setId(1L);
        List<Product> tempProducts = productCategory.getProducts();
        IntStream.range(0, tempProducts.size()).forEach(i->{
            tempProducts.get(i).setId((long)i);
        });
    }

}
