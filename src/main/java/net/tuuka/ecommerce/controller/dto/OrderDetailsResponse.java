package net.tuuka.ecommerce.controller.dto;

import lombok.Getter;
import lombok.ToString;
import net.tuuka.ecommerce.model.order.Address;
import net.tuuka.ecommerce.model.order.OrderProduct;

import java.util.List;

@Getter
@ToString
public class OrderDetailsResponse {

    private final OrderResponse order;
    private final List<OrderProduct> orderProducts;
    private final Address shippingAddress;
    private final AppUserRepresentation user;

    public OrderDetailsResponse(OrderResponse order,
                                Address address,
                                List<OrderProduct> orderProducts,
                                AppUserRepresentation user) {
        this.order = order;
        this.orderProducts = orderProducts;
        this.shippingAddress = address;
        this.user = user;
    }
}
