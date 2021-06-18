package net.tuuka.ecommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import net.tuuka.ecommerce.util.DateUtil;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.server.core.Relation;

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
@Relation(itemRelation = "product", collectionRelation = "products")
@JsonPropertyOrder({"id", "sku", "name", "description", "unitPrice"})
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double unitPrice;
    private String imageUrl;

    @Column(nullable = false)
    private Boolean active;

    private Integer unitsInStock;

    @Column(nullable = false)
    @CreationTimestamp
    @JsonSerialize(converter = DateUtil.ZonedDateTimeToStringConverter.class)
    @JsonDeserialize(converter = DateUtil.StringToZonedDateTimeConverter.class)
    private ZonedDateTime created;

    @Column(nullable = false)
    @UpdateTimestamp
    @JsonSerialize(converter = DateUtil.ZonedDateTimeToStringConverter.class)
    @JsonDeserialize(converter = DateUtil.StringToZonedDateTimeConverter.class)
    private ZonedDateTime lastUpdated;

    @ManyToOne
    @JoinColumn(name = "category_id",
            foreignKey = @ForeignKey(name = "product_category_fk"))
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//    @JsonBackReference
    @JsonIgnoreProperties({"products"})
//    @JsonIgnore
    @EqualsAndHashCode.Exclude
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
