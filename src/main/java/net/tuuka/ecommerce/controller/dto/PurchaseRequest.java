package net.tuuka.ecommerce.controller.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.tuuka.ecommerce.model.order.Address;
import net.tuuka.ecommerce.model.order.CreditCard;

import java.util.List;

@Data
public class PurchaseRequest {

    @Getter
    @Setter
    @ToString
    public static class OrderItem {
        private Long id;
        private Integer quantity;
//        private Double price;
    }

    private final List<OrderItem> orderItems;

    private final Address shippingAddress;

    private final CreditCard creditCard;

    private final String email;

}
