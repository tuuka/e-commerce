package net.tuuka.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

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
@Relation(itemRelation = "category", collectionRelation = "categories")
public class ProductCategory extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "category", orphanRemoval = true)
//    @JsonManagedReference
//    @JsonIgnore
    @JsonIgnoreProperties({"description", "unitsInStock", "unitPrice",
            "imageUrl", "created", "lastUpdated", "active", "category"})
    @ToString.Exclude
    private List<Product> products;

    public ProductCategory(String name) {
        this.name = name;
    }

}
