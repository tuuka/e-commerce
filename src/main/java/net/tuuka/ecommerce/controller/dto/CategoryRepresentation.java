package net.tuuka.ecommerce.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import net.tuuka.ecommerce.model.ProductCategory;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
public class CategoryRepresentation {

    @JsonIgnore
    private ProductCategory category = new ProductCategory();

    public CategoryRepresentation(ProductCategory category) {
        this.category = category;
    }

    public void setId(Long id) { category.setId(id); }

    public Long getId() { return category.getId(); }

    public void setName(String name) { category.setName(name); }

    @NotBlank(message = "Category name can't be blank")
    public String getName() { return category.getName(); }

    public ProductCategory getCategory() { return category; }

}
