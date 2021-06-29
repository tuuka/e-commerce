package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.dto.*;
import net.tuuka.ecommerce.model.order.OrderStatus;
import net.tuuka.ecommerce.model.user.AppUser;
import net.tuuka.ecommerce.model.user.AppUserRole;
import net.tuuka.ecommerce.service.AppUserService;
import net.tuuka.ecommerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AppUserService appUserService;

    @GetMapping("/status")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getOrderStatuses() {
        return ResponseEntity.ok(OrderStatus.values());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addOrder(@RequestBody PurchaseRequest purchaseRequest) {
        String orderNumber = orderService.placeOrder(purchaseRequest).getTrackingNumber();
        return ResponseEntity.ok(new SimpleMessageResponse("Order saved: " + orderNumber));
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateOrder(@RequestBody OrderRequest orderRequest) {
        String orderNumber = orderService.updateOrder(orderRequest).getTrackingNumber();
        return ResponseEntity.ok(new SimpleMessageResponse("Order updated: " + orderNumber));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public List<OrderResponse> getOrders(Authentication authentication) {
        AppUser loggedUser = appUserService.getAppUserByEmail(authentication.getName());
        if (loggedUser == null) throw new UsernameNotFoundException("User does not exist");
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean notACustomer = authorities.stream().anyMatch(auth ->
                auth.getAuthority().equals(AppUserRole.ROLE_ADMIN.name()) ||
                        auth.getAuthority().equals(AppUserRole.ROLE_MANAGER.name()));
        return notACustomer ? orderService.findAllRepresentation() :
                orderService.findByUserIdRepresentation(loggedUser.getId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MANAGER') or hasRole('ADMIN')")
    public OrderDetailsResponse getOrderById(@PathVariable("id") Long id, Authentication authentication) {
        AppUser loggedUser = appUserService.getAppUserByEmail(authentication.getName());
        if (loggedUser == null) throw new UsernameNotFoundException("User does not exist");
        return orderService.findByIdRepresentation(id);
    }

}
