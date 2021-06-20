package net.tuuka.ecommerce.model.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import net.tuuka.ecommerce.model.BaseEntity;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @JsonFormat(pattern = "dd/MM/yyyy")
    @CreationTimestamp
    private LocalDate dateCreated;

    private String status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "apartment", column = @Column(name = "apartment")),
            @AttributeOverride( name = "street", column = @Column(name = "street")),
            @AttributeOverride( name = "city", column = @Column(name = "city")),
            @AttributeOverride( name = "country", column = @Column(name = "country")),
            @AttributeOverride( name = "state", column = @Column(name = "state")),
            @AttributeOverride( name = "zip", column = @Column(name = "zip"))
    })
    private Address shippingAddress;

    @JsonManagedReference
    @OneToMany(mappedBy = "pk.order")
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @Transient
    public Double getTotalOrderPrice() {
        return getOrderProducts().stream()
                .map(orderProduct -> orderProduct.getProduct().getUnitPrice() * orderProduct.getQuantity())
                .reduce(0D, (acc, price) -> {
                    acc += price;
                    return acc;
                });
    }

    @Transient
    public int getNumberOfProducts() {
        return getOrderProducts().stream().map(OrderProduct::getQuantity)
                .reduce(0, (acc, quantity) -> {
                    acc += quantity;
                    return acc;
                });
    }

}
