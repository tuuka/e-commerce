package net.tuuka.ecommerce.controller;

import lombok.var;
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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.hateoas.server.core.TypeReferences;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@EnabledIf(value = "${app.test.integration_test_enabled}", loadContext = true)
public class ProductRestControllerIntegrationTest {

//    @LocalServerPort private int port;

    @Value("http://localhost:${local.server.port}${app.api.path}/products")
    private String productsUrl;

    @Value("http://localhost:${local.server.port}${app.api.path}/categories")
    private String categoriesUrl;

    @Autowired
    private TestRestTemplate restTemplate;

    private final List<ProductCategory> categories = new LinkedList<>(Arrays.asList(
            new ProductCategory("category_test_1"),
            new ProductCategory("category_test_2")
    ));

    private static final ParameterizedTypeReference<EntityModel<ProductCategory>> categoryEntityModelClass =
            ParameterizedTypeReference.forType(
                    new ParameterizedTypeReference<EntityModel<ProductCategory>>() {
                    }.getType());

    private static final ParameterizedTypeReference<EntityModel<Product>> productEntityModelClass =
            ParameterizedTypeReference.forType(
                    new ParameterizedTypeReference<EntityModel<Product>>() {
                    }.getType());

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

        var fetchedProductList = fetchProductList();
        assertEquals(0, fetchedProductList.size());

    }

    @Test
    @Order(2)
    void givenEmptyDB_whenGetAllCategories_shouldReturnStatusOKAndEmptyList() {

        var fetchedCategoriesList = fetchCategoriesList();
        assertEquals(0, fetchedCategoriesList.size());

    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    @Order(3)
    void givenCategoryWithNullId_whenSaveCategory_shouldReturnStatusCreatedAndSavedEntityWithId(int i) {

        ResponseEntity<EntityModel<ProductCategory>> categoryResponseEntity =
                restTemplate.exchange(categoriesUrl, HttpMethod.POST, new HttpEntity<>(categories.get(i)),
                        categoryEntityModelClass);
        assertSame(HttpStatus.CREATED, categoryResponseEntity.getStatusCode());
        ProductCategory savedCategory = Objects.requireNonNull(categoryResponseEntity.getBody()).getContent();
        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getId());

    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @Order(4)
    void givenNullIdProductWithCategory_whenSaveProduct_shouldReturnStatusCreatedAndSavedEntityWithId(int i) {

        products.get(i).setCategory(i < 2 ? savedCategories.get(0) : savedCategories.get(1));

        ResponseEntity<EntityModel<Product>> productResponseEntity =
                restTemplate.exchange(productsUrl, HttpMethod.POST, new HttpEntity<>(products.get(i)),
                        productEntityModelClass);
        assertSame(HttpStatus.CREATED, productResponseEntity.getStatusCode());
        Product savedProduct = Objects.requireNonNull(productResponseEntity.getBody()).getContent();
        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getId());

    }

    @Test
    @Order(10)
    void givenIdOfSavedProduct_whenGetById_shouldReturnProductWithCategory() {

        ResponseEntity<EntityModel<Product>> productResponseEntity =
                restTemplate.exchange(productsUrl + "/{id}", HttpMethod.GET, null,
                        productEntityModelClass, savedProducts.get(0).getId());
        assertSame(HttpStatus.OK, productResponseEntity.getStatusCode());
        Product fetchedProduct = Objects.requireNonNull(productResponseEntity.getBody()).getContent();
        assertNotNull(fetchedProduct);
        assertEquals(savedProducts.get(0).getSku(), fetchedProduct.getSku());
        assertEquals(savedProducts.get(0).getCategory().getName(), fetchedProduct.getCategory().getName());

    }

    @Test
    @Order(15)
    void givenIdOfSavedCategory_whenGetById_shouldReturnCategoryWithProducts() {

        ResponseEntity<EntityModel<ProductCategory>> categoryResponseEntity =
                restTemplate.exchange(categoriesUrl + "/{id}",
                        HttpMethod.GET, null,
                        categoryEntityModelClass,
                        savedCategories.get(0).getId());
        assertSame(HttpStatus.OK, categoryResponseEntity.getStatusCode());
        ProductCategory fetchedCategory = Objects.requireNonNull(categoryResponseEntity.getBody()).getContent();
        assertNotNull(fetchedCategory);
        assertEquals(savedCategories.get(0).getName(), fetchedCategory.getName());
        assertEquals(savedCategories.get(0).getProducts().size(), fetchedCategory.getProducts().size());

    }

    @Test
    @Order(20)
    void givenNameOfSavedCategory_whenSearchByName_shouldReturnCategory() {

        ResponseEntity<EntityModel<ProductCategory>> categoryResponseEntity =
                restTemplate.exchange(categoriesUrl + "/search?name={name}",
                        HttpMethod.GET, null,
                        categoryEntityModelClass, categories.get(1).getName());
        assertSame(HttpStatus.OK, categoryResponseEntity.getStatusCode());
        ProductCategory fetchedCategory = Objects.requireNonNull(categoryResponseEntity.getBody()).getContent();
        assertNotNull(fetchedCategory);
        assertEquals(2, fetchedCategory.getProducts().size());
//        assertEquals(savedCategories.get(1).getProducts(), fetchedCategory.getProducts());

    }

    @Test
    @Order(25)
    void givenPartOfNameOfSavedProduct_whenSearchByName_shouldReturnListOfMatchedProducts() {

        ResponseEntity<CollectionModel<EntityModel<Product>>> productListResponseEntity =
                restTemplate.exchange(productsUrl+"/search?name={name}", HttpMethod.GET, null,
                new ParameterizedTypeReference<CollectionModel<EntityModel<Product>>>() {},
                        "ame_test_2");

        assertSame(HttpStatus.OK, productListResponseEntity.getStatusCode());
        List<Product> fetchedProducts = getProductsFromCollectionModel(Objects.
                requireNonNull(productListResponseEntity.getBody()));
        assertNotNull(fetchedProducts);
        assertEquals(2, fetchedProducts.size());

    }

    @Test
    @Order(30)
    void givenPartOfSkuOfSavedProduct_whenSearchBySku_shouldReturnListOfMatchedProducts() {

        ResponseEntity<CollectionModel<EntityModel<Product>>> productListResponseEntity =
                restTemplate.exchange(productsUrl+"/search?sku={sku}", HttpMethod.GET, null,
                        new ParameterizedTypeReference<CollectionModel<EntityModel<Product>>>() {},
                        "ku_test_1");
        assertSame(HttpStatus.OK, productListResponseEntity.getStatusCode());
        List<Product> fetchedProducts = getProductsFromCollectionModel(Objects
                .requireNonNull(productListResponseEntity.getBody()));
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
        ResponseEntity<EntityModel<ProductCategory>> categoryResponseEntity =
                restTemplate.exchange(categoriesUrl + "/{id}",
                        HttpMethod.PUT, categoryEntity,
                        categoryEntityModelClass, savedCategories.get(0).getId());
        assertSame(HttpStatus.OK, categoryResponseEntity.getStatusCode());

        List<ProductCategory> fetchedCategoryList = fetchCategoriesList();
        assertEquals(1, fetchedCategoryList.size());
        assertEquals(1, fetchedCategoryList.get(0).getProducts().size());

    }


    private List<Product> fetchProductList() {

        Traverson traverson = new Traverson(URI.create(productsUrl), MediaTypes.HAL_JSON);
        return getProductsFromCollectionModel(Objects.requireNonNull(traverson.
                follow("self").
                toObject(new TypeReferences.CollectionModelType<EntityModel<Product>>() {})));
    }

    private List<ProductCategory> fetchCategoriesList() {

        Traverson traverson = new Traverson(URI.create(categoriesUrl), MediaTypes.HAL_JSON);
        return Objects.requireNonNull(traverson.
                follow("self").
                toObject(new TypeReferences.CollectionModelType<EntityModel<ProductCategory>>() {}))
                .getContent().stream().map(EntityModel::getContent).collect(Collectors.toList());

    }

    private List<Product> getProductsFromCollectionModel(CollectionModel<EntityModel<Product>> collectionModel){
        return collectionModel.getContent().stream()
                .map(EntityModel::getContent)
                .collect(Collectors.toList());
    }

}
