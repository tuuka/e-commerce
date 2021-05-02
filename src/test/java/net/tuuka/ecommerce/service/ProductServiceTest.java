package net.tuuka.ecommerce.service;

/*
    ProductService should provide at least all CRUD operation on Product repository.
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
        products =
                new ArrayList<>(FakeProductGenerator.getFakeProductList());
        productCategories =
                new ArrayList<>(FakeProductGenerator.getFakeProductCategoriesList());
    }

    @AfterEach
    void tearDown() {
        products = null;
        productCategories = null;
    }

    @Test
    void givenProductWithCat_whenSave_ShouldSaveBoth() {

        // given product with category
        Product product = products.get(0);
        given(productRepository.save(isA(Product.class)))
                .will((InvocationOnMock invocation) -> {
                    Product tempProduct = invocation.getArgument(0);
                    tempProduct.setId(1L);
                    return tempProduct;
                });
        given(productCategoryRepository.save(isA(ProductCategory.class)))
                .will((InvocationOnMock invocation) -> {
                    ProductCategory tempCategory = invocation.getArgument(0);
                    tempCategory.setId(1L);
                    return tempCategory;
                });

        // when save
        Product savedProduct = productService.save(product);

        // then should get saved product back with category
        assertEquals(product, savedProduct);
        assertEquals(1, savedProduct.getId());
        assertEquals(1, savedProduct.getCategory().getId());
        then(productRepository).should().save(eq(product));
        then(productCategoryRepository).should().save(eq(product.getCategory()));

    }

}
