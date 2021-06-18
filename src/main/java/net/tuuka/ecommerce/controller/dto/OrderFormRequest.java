package net.tuuka.ecommerce.controller.dto;


import lombok.*;
import net.tuuka.ecommerce.model.order.Address;
import net.tuuka.ecommerce.model.order.CreditCard;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class OrderFormRequest {

    @Getter
    @Setter
    @ToString
    public static class CartItem {
        private Long id;
        private String name;
        private Integer quantity;
    }

    private List<CartItem> cartItems;

    private Address shippingAddress;

    private CreditCard creditCard;

    private String email;

}
