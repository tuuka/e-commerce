package net.tuuka.ecommerce.service;

import net.tuuka.ecommerce.controller.dto.AppUserRepresentation;
import net.tuuka.ecommerce.controller.dto.OrderDetailsResponse;
import net.tuuka.ecommerce.controller.dto.OrderResponse;
import net.tuuka.ecommerce.controller.dto.PurchaseRequest;
import net.tuuka.ecommerce.dao.OrderRepository;
import net.tuuka.ecommerce.model.order.Order;
import net.tuuka.ecommerce.model.order.OrderProduct;
import net.tuuka.ecommerce.model.order.OrderStatus;
import net.tuuka.ecommerce.model.user.AppUser;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService extends BaseCrudAbstractService<Order, Long, OrderRepository> {

    private final ProductService productService;
    private final AppUserService appUserService;

    public OrderService(OrderRepository repository,
                        ProductService productService,
                        AppUserService appUserService) {
        super(repository);
        this.productService = productService;
        this.appUserService = appUserService;
    }

    public Order placeOrder(@NotNull(message = "Empty purchase request is not allowed") PurchaseRequest purchaseRequest) {

        AppUser appUser = appUserService.getAppUserByEmail(purchaseRequest.getEmail());
        if (appUser == null) throw new IllegalStateException("User not found");
        if (purchaseRequest.getOrderItems().size() == 0) throw new IllegalStateException("Empty order item list");
        Order order = new Order();
        order.setAppUser(appUser);
        order.setTrackingNumber(UUID.randomUUID().toString());
        List<OrderProduct> orderProducts = purchaseRequest.getOrderItems()
                .stream()
                .map(item -> new OrderProduct(
                        order,
                        productService.findById(item.getId()),
                        item.getQuantity()))
                .collect(Collectors.toList());
        order.setOrderProducts(orderProducts);
        order.setStatus(OrderStatus.NEW);
        order.setShippingAddress(purchaseRequest.getShippingAddress());
        return repository.save(order);

    }

    public List<OrderResponse> findAllRepresentation() {
        List<Order> orders = super.findAll();
        return orders.stream().map(OrderResponse::new).collect(Collectors.toList());
    }

    public List<OrderResponse> findByUserIdRepresentation(Long id) {
        return repository.findAllByAppUserId(id).stream()
                .map(OrderResponse::new).collect(Collectors.toList());
    }

    public OrderDetailsResponse findByIdRepresentation(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Order with id= " + id + " not found."));

        return new OrderDetailsResponse(
                new OrderResponse(order),
                order.getShippingAddress(),
                order.getOrderProducts(),
                new AppUserRepresentation(order.getAppUser()));
    }
}
