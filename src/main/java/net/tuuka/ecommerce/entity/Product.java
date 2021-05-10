package net.tuuka.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Entity
@Table(name = "product", uniqueConstraints = {
        @UniqueConstraint(name = "product_sku", columnNames = "sku")
})
public class Product extends BaseEntity{

    @Column(nullable = false)
    private String sku;                 // not null

    @Column(nullable = false)
    private String name;                // not null

    private String description;

    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double unitPrice;
    private String imageUrl;

    @Column(nullable = false)
    private Boolean active;             // not null

    private Integer unitsInStock;

    @Column(nullable = false)
    @CreationTimestamp
    private ZonedDateTime created;      // not null

    @Column(nullable = false)
    @UpdateTimestamp
    private ZonedDateTime lastUpdated;  // not null

    @ManyToOne
    @JoinColumn(name = "category_id",
            foreignKey = @ForeignKey(name = "product_category_fk"))
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonBackReference
    private ProductCategory category;

    public Product(String sku,
                   String name,
                   String description,
                   Double unitPrice,
                   String imageUrl,
                   Boolean active,
                   Integer unitsInStock) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.imageUrl = imageUrl;
        this.active = active;
        this.unitsInStock = unitsInStock;
    }
}
