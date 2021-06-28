package net.tuuka.ecommerce.model.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.tuuka.ecommerce.model.BaseEntity;
import net.tuuka.ecommerce.model.user.AppUser;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @JsonFormat(pattern = "dd/MM/yyyy")
    @CreationTimestamp
    private LocalDate dateCreated;

    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride(name = "apartment", column = @Column(name = "apartment")),
//            @AttributeOverride(name = "street", column = @Column(name = "street")),
//            @AttributeOverride(name = "city", column = @Column(name = "city")),
//            @AttributeOverride(name = "country", column = @Column(name = "country")),
//            @AttributeOverride(name = "state", column = @Column(name = "state")),
//            @AttributeOverride(name = "zip", column = @Column(name = "zip"))
//    })
    private Address shippingAddress;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser appUser;

    @Transient
    public Double getTotalOrderPrice() {
        return getOrderProducts().stream()
                .map(OrderProduct::getTotalPrice)
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
