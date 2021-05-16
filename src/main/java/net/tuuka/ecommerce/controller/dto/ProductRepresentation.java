package net.tuuka.ecommerce.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.entity.Product;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class ProductRepresentation extends RepresentationModel<ProductRepresentation> {

    @JsonIgnore
    private final Product product = new Product();

    public Product getProduct() {
        return product;
    }

    @NotBlank(message = "Can't be blank")
    public String getSku() {
        return product.getSku();
    }

    public void setSku(String sku) {
        product.setSku(sku);
    }

    @NotBlank(message = "Can't be blank")
    public String getName() {
        return product.getName();
    }

    public void setName(String name) {
        product.setName(name);
    }

    public String getDescription() {
        return product.getDescription();
    }

    public void setDescription(String description) {
        product.setDescription(description);
    }

    @DecimalMin(value = "0.0", message = "Can't be less then 0.0")
    public Double getUnitPrice() {
        return product.getUnitPrice();
    }

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

    @Min(value = 0, message = "Can't be less then 0")
    public Integer getUnitsInStock() {
        return product.getUnitsInStock();
    }

    public void setUnitsInStock(Integer unitsInStock) {
        product.setUnitsInStock(unitsInStock);
    }
}
