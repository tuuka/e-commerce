package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.controller.dto.PurchaseRequest;
import net.tuuka.ecommerce.controller.dto.SimpleMessageResponse;
import net.tuuka.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addOrder(@RequestBody PurchaseRequest purchaseRequest) {
        System.out.println(purchaseRequest);
        String orderNumber = orderService.placeOrder(purchaseRequest).getTrackingNumber();
        return ResponseEntity.ok(new SimpleMessageResponse("Order saved: " + orderNumber));
    }

}
