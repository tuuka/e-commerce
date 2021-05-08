package net.tuuka.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

//@Data  // bug with circular reference in toString()
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "product_category",
        uniqueConstraints = {
                @UniqueConstraint(name = "product_category_name", columnNames = "name")
        })
public class ProductCategory {

    @Id
    @SequenceGenerator(name = "productCategorySequence",
            sequenceName = "product_category_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "productCategorySequence")
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "category", orphanRemoval = true)
    @JsonManagedReference
    private List<Product> products;

    public ProductCategory(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ProductCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
