package net.tuuka.ecommerce.service;

/*
    ProductCategoryService should provide at least all CRUD operation on ProductCategory repository.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@SpringBootTest(classes = {ProductService.class})
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
    void givenCategoryId_whenGetAllCategories_shouldReturnCategoryList() {

        // given
        given(productCategoryRepository.findAll()).willReturn(categories);

        // when
        List<ProductCategory> fetchedCategories = categoryService.getAllCategories();

        // then
        assertEquals(categories, fetchedCategories);
        assertEquals(categories.get(0).getProducts(), fetchedCategories.get(0).getProducts());
        then(productCategoryRepository).should().findAll();

    }


}
