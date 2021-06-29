package net.tuuka.ecommerce.dto;

import lombok.Data;

@Data
public class OrderItem {

    private Long productId;
    private Integer quantity;
//        private Double price;

}
