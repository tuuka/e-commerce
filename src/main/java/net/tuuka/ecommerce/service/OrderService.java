package net.tuuka.ecommerce.service;

import net.tuuka.ecommerce.dao.OrderRepository;
import net.tuuka.ecommerce.model.order.Order;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class OrderService extends BaseCrudAbstractService<Order, Long, OrderRepository> {

    public OrderService(OrderRepository repository) {
        super(repository);
    }
}
