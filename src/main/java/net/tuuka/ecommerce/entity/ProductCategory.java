package net.tuuka.ecommerce.entity;

import lombok.Data;

@Data
public class ProductCategory {
    private Long id;
    private String name;

    public ProductCategory(String name) {
        this.name = name;
    }
}
