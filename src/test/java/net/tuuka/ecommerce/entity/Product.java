package net.tuuka.ecommerce.entity;

import lombok.*;

import java.time.ZonedDateTime;

@Setter
@Getter
@EqualsAndHashCode
@ToString
public class Product {
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

    public Product(String sku,
                   String name,
                   String description,
                   Double unitPrice,
                   String imageUrl,
                   Boolean active,
                   Integer unitsInStock) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.active = active;
        this.unitsInStock = unitsInStock;
        this.setCreated(ZonedDateTime.now());
        this.setLastUpdated(this.getCreated());
    }
}
