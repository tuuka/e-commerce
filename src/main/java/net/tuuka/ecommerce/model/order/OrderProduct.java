package net.tuuka.ecommerce.model.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.tuuka.ecommerce.model.BaseEntity;
import net.tuuka.ecommerce.model.product.Product;

import javax.persistence.*;
import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_product")
public class OrderProduct extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    @Transient
    public Double getTotalPrice() {
        return getProduct().getUnitPrice() * getQuantity();
    }

    @Override
    public String toString() {
        return "OrderProduct{" +
                "id=" + id +
                ", order=" + order.getId() +
                ", product=" + product.getId() + "-" + product.getName() +
                ", quantity=" + quantity +
                '}';
    }

}
