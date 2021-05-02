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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
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
    void setUp(){
        products =
                new ArrayList<>(FakeProductGenerator.getFakeProductList());
        productCategories =
                new ArrayList<>(FakeProductGenerator.getFakeProductCategoriesList());
    }

    @AfterEach
    void tearDown(){
        products = null;
        productCategories = null;
    }

    @Test
    void givenProductWithCat_whenSave_ShouldSaveBoth(){
        // given

        // when

        // then
    }

}
