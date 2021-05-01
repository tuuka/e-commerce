package net.tuuka.ecommerce.entity;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
    Just checking that Product and Category classes have necessary fields.
    Assume Product class has following fields:
        private Long id;
        private String sku;                 // not null
        private String name;                // not null
        private String description;
        private Double unitPrice;
        private String imageUrl;
        private Boolean active;             // not null
        private Integer unitsInStock;
        private ZonedDateTime created;      // not null
        private ZonedDateTime lastUpdated;  // not null
        private Category category;

    And Category class has fields:
        private Long id;
        private String name;                // not null
*/

class ProductAndCategoryEntitiesTest {

    static private final String sku = "someSKU_13245#";
    static private final String name = "some name (может быть и UTF-8)";
    static private final String description = "some description";
    static private final Double unitPrice = 123.99;
    static private final String imageUrl = "/static/images/cat1/1.jpg";
    static private final Boolean active = true;
    static private final Integer unitsInStock = 198;

    @Test
    void givenProductData_whenCreatingProduct_shouldCreateProductWithDateOfCreationAndUpdate() {
        // given
        // When creating Product date of creation and date od update have to be set
        // automatically if absent in constructor args

        // when
        ZonedDateTime start = ZonedDateTime.now().minusSeconds(2);
        Product product = new Product(
                sku,
                name,
                description,
                unitPrice,
                imageUrl,
                active,
                unitsInStock
        );
        ZonedDateTime end = ZonedDateTime.now().plusSeconds(2);

        // then
        assertTrue(start.isBefore(product.getCreated()),
                "DateTime of creation must point to time after constructor call.");
        assertTrue(end.isAfter(product.getCreated()),
                "DateTime of creation must point to time before " +
                        "object is completely constructed.");
        assertTrue(product.getCreated().isEqual(product.getLastUpdated()),
                "Creation time and lastUpdate time must be equal when Product" +
                        "just instantiated.");
        assertEquals(name, product.getName(), "Saved and read name must be equal. " +
                "May be should check UTF-8 support.");
    }

    @Test
    void givenProductAndCategory_whenCreatingProduct_shouldCreateProductWithCategory() {
        // given
        String categoryName = "some category";

        // when
        ProductCategory productCategory = new ProductCategory(categoryName);
        Product product = new Product(
                sku,
                name,
                description,
                unitPrice,
                imageUrl,
                active,
                unitsInStock
        );
        product.setCategory(productCategory);

        // then
        assertEquals(categoryName, product.getCategory().getName());
    }

}
