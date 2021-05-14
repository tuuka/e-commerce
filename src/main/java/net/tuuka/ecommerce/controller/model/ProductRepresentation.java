package net.tuuka.ecommerce.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.tuuka.ecommerce.entity.Product;
import org.hibernate.validator.constraints.Range;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;
import org.springframework.hateoas.mediatype.hal.forms.Jackson2HalFormsModule;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.*;

public class ProductRepresentation extends RepresentationModel<ProductRepresentation> {

    @JsonIgnore
    private final Product product = new Product();

    private String sku;
    private String name;
    private String description;
    private Double unitPrice;
    private String imageUrl;
    private Boolean active;
    private Integer unitsInStock;

    public Product getProduct() {
        return product;
    }

    public String getSku() {
        return product.getSku();
    }

    @NotBlank(message = "Can't be blank")
    public void setSku(String sku) {
        product.setSku(sku);
    }

    public String getName() {
        return product.getName();
    }

    @NotBlank(message = "Can't be blank")
    public void setName(String name) {
        product.setName(name);
    }

    public String getDescription() {
        return product.getDescription();
    }

    public void setDescription(String description) {
        product.setDescription(description);
    }

    public Double getUnitPrice() {
        return product.getUnitPrice();
    }

    @DecimalMin(value = "0.0", message = "Can't be less then 0.0")
    public void setUnitPrice(Double unitPrice) {
        product.setUnitPrice(unitPrice);
    }

    public String getImageUrl() {
        return product.getImageUrl();
    }

    public void setImageUrl(String imageUrl) {
        product.setImageUrl(imageUrl);
    }

    public Boolean getActive() {
        return product.getActive();
    }

    public void setActive(Boolean active) {
        product.setActive(active);
    }

//    public Integer getUnitsInStock() {
//        return unitsInStock;
//    }

    @Min(value = 0, message = "Can't be less then 0")
    public void setUnitsInStock(Integer unitsInStock) {
        product.setUnitsInStock(unitsInStock);
    }
}
