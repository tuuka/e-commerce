package net.tuuka.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@Entity
@Table(name = "product_category",
        uniqueConstraints = {
                @UniqueConstraint(name = "product_category_name", columnNames = "name")
        })
public class ProductCategory extends BaseEntity{

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "category", orphanRemoval = true)
//    @JsonManagedReference
    @JsonIgnore
    @ToString.Exclude
    private List<Product> products;

    public ProductCategory(String name) {
        this.name = name;
    }

}
