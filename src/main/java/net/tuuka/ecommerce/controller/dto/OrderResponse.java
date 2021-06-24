package net.tuuka.ecommerce.controller.dto;

import lombok.Getter;
import net.tuuka.ecommerce.model.order.Order;
import net.tuuka.ecommerce.model.order.OrderStatus;

import java.time.LocalDate;

@Getter
public class OrderResponse {

    private final Long id;
    private final String trackingNumber;
    private final LocalDate created;
    private final OrderStatus status;
    private final Integer totalQuantity;
    private final Double totalPrice;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.trackingNumber = order.getTrackingNumber();
        this.created = order.getDateCreated();
        this.status = order.getStatus();
        this.totalQuantity = order.getNumberOfProducts();
        this.totalPrice = order.getTotalOrderPrice();
    }
}
