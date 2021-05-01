package net.tuuka.ecommerce.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

/*
    Just checking that Product class has necessary fields and that fields
    are not null.
    Assume Product class has next fields:
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

    static private String sku = "someSKU_13245#";
    static private String name = "some name (может быть и UTF-8)";
    static private String description = "some description";
    static private Double unitPrice = 123.99;
    static private String imageUrl = "/static/images/cat1/1.jpg";
    static private Boolean active = true;
    static private Integer unitsInStock = 198;
    static private ZonedDateTime created = ZonedDateTime.now();
    static private ZonedDateTime lastUpdated = ZonedDateTime.now().plusMinutes(10);

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
        System.out.println(start.isBefore(product.getCreated()));
        assertTrue(start.isBefore(product.getCreated()),
                "DateTime of creation must point to time after constructor call.");
        assertTrue(end.isAfter(product.getCreated()),
                "DateTime of creation must point to time before " +
                        "object is completely constructed.");
        assertTrue(product.getCreated().isEqual(product.getLastUpdated()),
                "Creation time and lastUpdate time must be equal when Product" +
                        "just instantiated.");
    }

}
