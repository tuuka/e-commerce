package net.tuuka.ecommerce.controller;

import net.tuuka.ecommerce.controller.dto.OrderFormRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addOrder(@RequestBody OrderFormRequest orderFormRequest) {
        System.out.println(orderFormRequest);
        return ResponseEntity.ok(orderFormRequest);
    }

}
