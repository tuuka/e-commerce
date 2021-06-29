package net.tuuka.ecommerce.dto;


import lombok.Data;
import net.tuuka.ecommerce.model.order.Address;
import net.tuuka.ecommerce.model.order.CreditCard;

import java.util.List;

@Data
public class PurchaseRequest {

    private final List<OrderItem> orderItems;

    private final Address shippingAddress;

    private final CreditCard creditCard;

    private final String email;

}
