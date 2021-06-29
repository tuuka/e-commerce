package net.tuuka.ecommerce.dto;

import lombok.Data;
import net.tuuka.ecommerce.model.order.Address;

import java.util.List;

@Data
public class OrderRequest {

    private final Long orderId;
    private final String status;
    private final Address shippingAddress;
    private final List<OrderItem> orderItems;

}
