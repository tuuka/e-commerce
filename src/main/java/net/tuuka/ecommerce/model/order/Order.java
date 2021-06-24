package net.tuuka.ecommerce.model.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import net.tuuka.ecommerce.model.BaseEntity;
import net.tuuka.ecommerce.model.user.AppUser;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@Entity
@Table(name = "orders")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "orderProducts")
public class Order extends BaseEntity {

    @JsonFormat(pattern = "dd/MM/yyyy")
    @CreationTimestamp
    private LocalDate dateCreated;

    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "apartment", column = @Column(name = "apartment")),
            @AttributeOverride(name = "street", column = @Column(name = "street")),
            @AttributeOverride(name = "city", column = @Column(name = "city")),
            @AttributeOverride(name = "country", column = @Column(name = "country")),
            @AttributeOverride(name = "state", column = @Column(name = "state")),
            @AttributeOverride(name = "zip", column = @Column(name = "zip"))
    })
    private Address shippingAddress;

    @JsonManagedReference
    @OneToMany(mappedBy = "pk.order", cascade = CascadeType.ALL)
    private Set<OrderProduct> orderProducts = new HashSet<>();

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
