package net.tuuka.ecommerce.service;

/*
    ProductService should provide at least all CRUD operation on Product repository.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
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
        // using lists of product and categories with null ids
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
    void givenProductWithCat_whenSave_shouldSaveBoth() {

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
        setIds(products);
        long id = products.get(0).getId();
        given(productRepository.findById(eq(id)))
                .willReturn(Optional.of(products.get(0)));

        // when
        Product fetchedProduct = productService.getProductById(id);

        // then
        assertEquals(products.get(0), fetchedProduct);
        then(productRepository).should().findById(eq(id));

    }

    @Test
    void givenNonExistingProductId_whenGetProductById_shouldReturnNull() {

        // given
        given(productRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        Product fetchedProduct = productService.getProductById(1L);

        // then
        assertTrue(fetchedProduct == null);
        then(productRepository).should().findById(anyLong());

    }


    // Repositories assign ids automatically. As we mocking repos in
    // this test class we assign ids manually
    private void setIds(List<Product> productList) {
        IntStream.range(0, productList.size()).forEach(i ->
        {
            productList.get(i).setId((long) i + 1);
            ProductCategory cat = productList.get(i).getCategory();
            cat.setId(Long.parseLong(cat.getName().split("_")[1]));
        });
    }

}
