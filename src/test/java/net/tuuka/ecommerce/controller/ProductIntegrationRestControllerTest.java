package net.tuuka.ecommerce.controller;

import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnabledIf(value = "${app.test.rest_integration_test_enabled}", loadContext = true)
public class ProductIntegrationRestControllerTest {

//    @LocalServerPort private int port;

    @Value("http://localhost:${local.server.port}${app.api.path}${app.api.active_version}/products")
    private String productsUrl;

    @Value("http://localhost:${local.server.port}${app.api.path}${app.api.active_version}/product_categories")
    private String categoriesUrl;

    @Autowired
    private TestRestTemplate restTemplate;

    private final List<ProductCategory> categories = new LinkedList<>(Arrays.asList(
            new ProductCategory("category_test_1"),
            new ProductCategory("category_test_2")
    ));
    private static final List<ProductCategory> savedCategories = new LinkedList<>();
    private static final List<Product> savedProducts = new LinkedList<>();
    private final List<Product> products;

    {
        products = new LinkedList<>(Arrays.asList(
                new Product(
                        "sku_test_11",
                        "name_test_11",
                        "desc_test_11",
                        1.,
                        "img-url_test_11",
                        true,
                        100
                ),
                new Product(
                        "sku_test_12",
                        "name_test_12",
                        "desc_test_12",
                        1.,
                        "img-url_test_12",
                        true,
                        100
                ),
                new Product(
                        "sku_test_21",
                        "name_test_21",
                        "desc_test_21",
                        1.,
                        "img-url_test_21",
                        true,
                        100
                ),
                new Product(
                        "sku_test_22",
                        "name_test_22",
                        "desc_test_22",
                        1.,
                        "img-url_test_22",
                        true,
                        100
                )
        ));
    }

    @BeforeEach
    void setUp() {
        savedCategories.clear();
        savedCategories.addAll(fetchCategoriesList());
        savedProducts.clear();
        savedProducts.addAll(fetchProductList());
    }

    @Test
    @Order(1)
    void givenEmptyDB_whenGetAllProducts_shouldReturnStatusOKAndEmptyList() {

        List<Product> fetchedProductList = fetchProductList();
        assertEquals(0, fetchedProductList.size());

    }

    @Test
    @Order(2)
    void givenEmptyDB_whenGetAllCategories_shouldReturnStatusOKAndEmptyList() {

        ResponseEntity<List<ProductCategory>> categoryListResponse =
                restTemplate.exchange(categoriesUrl, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<ProductCategory>>() {
                        });
        assertSame(HttpStatus.OK, categoryListResponse.getStatusCode());
        assertNotNull(categoryListResponse.getBody());
        assertEquals(0, categoryListResponse.getBody().size());

    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    @Order(3)
    void givenCategoryWithNullId_whenSaveCategory_shouldReturnStatusCreatedAndSavedEntityWithId(int i) {

        ResponseEntity<ProductCategory> categoryResponseEntity =
                restTemplate.postForEntity(categoriesUrl, categories.get(i), ProductCategory.class);
        assertSame(HttpStatus.CREATED, categoryResponseEntity.getStatusCode());
        ProductCategory savedCategory = categoryResponseEntity.getBody();
        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getId());
//        savedCategories.add(savedCategory);

    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @Order(4)
    void givenNullIdProductWithCategory_whenSaveProduct_shouldReturnStatusCreatedAndSavedEntityWithId(int i) {

        products.get(i).setCategory(i < 2 ? savedCategories.get(0) : savedCategories.get(1));

        ResponseEntity<Product> productResponseEntity =
                restTemplate.postForEntity(productsUrl, products.get(i), Product.class);
        assertSame(HttpStatus.CREATED, productResponseEntity.getStatusCode());
        Product savedProduct = productResponseEntity.getBody();
        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getId());
//        savedProducts.add(savedProduct);

    }

    @Test
    @Order(10)
    void givenIdOfSavedProduct_whenGetById_shouldReturnProductWithCategory() {

        ResponseEntity<Product> productResponseEntity =
                restTemplate.getForEntity(productsUrl + "/{id}", Product.class,
                        savedProducts.get(0).getId());
        assertSame(HttpStatus.OK, productResponseEntity.getStatusCode());
        Product fetchedProduct = productResponseEntity.getBody();
        assertNotNull(fetchedProduct);
        assertEquals(savedProducts.get(0).getSku(), fetchedProduct.getSku());
        assertEquals(savedProducts.get(0).getCategory().getName(), fetchedProduct.getCategory().getName());

    }

    @Test
    @Order(15)
    void givenIdOfSavedCategory_whenGetById_shouldReturnCategoryWithProducts() {

        ResponseEntity<ProductCategory> categoryResponseEntity =
                restTemplate.getForEntity(categoriesUrl + "/{id}",
                        ProductCategory.class, savedCategories.get(0).getId());
        assertSame(HttpStatus.OK, categoryResponseEntity.getStatusCode());
        ProductCategory fetchedCategory = categoryResponseEntity.getBody();
        assertNotNull(fetchedCategory);
        assertEquals(savedCategories.get(0).getName(), fetchedCategory.getName());
        assertEquals(savedCategories.get(0).getProducts(), fetchedCategory.getProducts());

    }

    @Test
    @Order(20)
    void givenNameOfSavedCategory_whenSearchByName_shouldReturnCategory() {

        ResponseEntity<ProductCategory> categoryResponseEntity =
                restTemplate.getForEntity(categoriesUrl + "/search?name={name}",
                        ProductCategory.class, categories.get(1).getName());
        assertSame(HttpStatus.OK, categoryResponseEntity.getStatusCode());
        ProductCategory fetchedCategory = categoryResponseEntity.getBody();
        assertNotNull(fetchedCategory);
        assertEquals(2, fetchedCategory.getProducts().size());
        assertEquals(savedCategories.get(1).getProducts(), fetchedCategory.getProducts());

    }

    @Test
    @Order(25)
    void givenPartOfNameOfSavedProduct_whenSearchByName_shouldReturnListOfMatchedProducts() {

        ResponseEntity<List<Product>> productListResponseEntity =
                restTemplate.exchange(productsUrl + "/search?name={name}",
                        HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<Product>>() {
                        },
                        "ame_test_2");
        assertSame(HttpStatus.OK, productListResponseEntity.getStatusCode());
        List<Product> fetchedProducts = productListResponseEntity.getBody();
        assertNotNull(fetchedProducts);
        assertEquals(2, fetchedProducts.size());

    }

    @Test
    @Order(30)
    void givenPartOfSkuOfSavedProduct_whenSearchBySku_shouldReturnListOfMatchedProducts() {

        ResponseEntity<List<Product>> productListResponseEntity =
                restTemplate.exchange(productsUrl + "/search?sku={sku}",
                        HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<Product>>() {
                        },
                        "ku_test_1");
        assertSame(HttpStatus.OK, productListResponseEntity.getStatusCode());
        List<Product> fetchedProducts = productListResponseEntity.getBody();
        assertNotNull(fetchedProducts);
        assertEquals(2, fetchedProducts.size());

    }

    @Test
    @Order(35)
    void givenProductWithChangedCategory_whenUpdateProduct_shouldUpdateAndReturn() {

        savedProducts.get(3).setCategory(savedCategories.get(0));
        HttpEntity<Product> productEntity = new HttpEntity<>(savedProducts.get(3));
        ResponseEntity<Product> productResponseEntity =
                restTemplate.exchange(productsUrl + "/{id}", HttpMethod.PUT,
                        productEntity, Product.class, savedProducts.get(3).getId());
        assertSame(HttpStatus.OK, productResponseEntity.getStatusCode());
        Product updatedProduct = productResponseEntity.getBody();
        assertNotNull(updatedProduct);
        assertEquals(savedCategories.get(0).getName(), updatedProduct.getCategory().getName());

    }

    @Test
    @Order(40)
    void givenProductId_whenDeleteProduct_shouldDeleteAndReturnDeleted() {

        ResponseEntity<Product> productResponseEntity =
                restTemplate.exchange(productsUrl + "/{id}", HttpMethod.DELETE,
                        null, Product.class, savedProducts.get(3).getId());
        assertSame(HttpStatus.OK, productResponseEntity.getStatusCode());
        Product deletedProduct = productResponseEntity.getBody();
        assertNotNull(deletedProduct);

        List<Product> fetchedProductList = fetchProductList();
        assertEquals(3, fetchedProductList.size());

    }

    @Test
    @Order(45)
    void givenCategoryId_whenDeleteCategoryWithProducts_shouldReturnNotAcceptable() {

        ResponseEntity<ProductCategory> categoryResponseEntity =
                restTemplate.exchange(categoriesUrl + "/{id}",
                        HttpMethod.DELETE, null,
                        ProductCategory.class, savedCategories.get(0).getId());
        assertSame(HttpStatus.NOT_ACCEPTABLE, categoryResponseEntity.getStatusCode());

    }

    @Test
    @Order(50)
    void givenCategoryId_whenForceDeleteCategoryWithProducts_shouldDeleteCategoryWithProducts() {

        ResponseEntity<ProductCategory> categoryResponseEntity =
                restTemplate.exchange(categoriesUrl + "/{id}?force=true",
                        HttpMethod.DELETE, null,
                        ProductCategory.class, savedCategories.get(0).getId());
        assertSame(HttpStatus.OK, categoryResponseEntity.getStatusCode());

        assertEquals(1, fetchProductList().size());

    }

    @Test
    @Order(55)
    void givenChangedCategory_whenUpdateCategory_shouldUpdateAndReturn() {

        savedCategories.get(0).getProducts().add(products.get(0));
        savedCategories.get(0).setName("another name");
        HttpEntity<ProductCategory> categoryEntity = new HttpEntity<>(savedCategories.get(0));
        ResponseEntity<ProductCategory> categoryResponseEntity =
                restTemplate.exchange(categoriesUrl + "/{id}",
                        HttpMethod.PUT, categoryEntity,
                        ProductCategory.class, savedCategories.get(0).getId());
        assertSame(HttpStatus.OK, categoryResponseEntity.getStatusCode());

        List<ProductCategory> fetchedCategoryList = fetchCategoriesList();
        assertEquals(1, fetchedCategoryList.size());
        assertEquals(1, fetchedCategoryList.get(0).getProducts().size());

    }


    private List<Product> fetchProductList() {

        ResponseEntity<List<Product>> productListResponse =
                restTemplate.exchange(productsUrl, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<Product>>() {
                        });
        assertSame(HttpStatus.OK, productListResponse.getStatusCode());
        assertNotNull(productListResponse.getBody());
        return productListResponse.getBody();

    }

    private List<ProductCategory> fetchCategoriesList() {

        ResponseEntity<List<ProductCategory>> categoriesListResponse =
                restTemplate.exchange(categoriesUrl, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<ProductCategory>>() {
                        });
        assertSame(HttpStatus.OK, categoriesListResponse.getStatusCode());
        assertNotNull(categoriesListResponse.getBody());
        return categoriesListResponse.getBody();

    }

}
