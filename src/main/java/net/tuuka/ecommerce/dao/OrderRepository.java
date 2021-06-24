package net.tuuka.ecommerce.dao;

import net.tuuka.ecommerce.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByAppUserId(Long id);

}
