package net.tuuka.ecommerce.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.tuuka.ecommerce.entity.BaseEntity;
import net.tuuka.ecommerce.entity.ProductCategory;
import org.springframework.hateoas.RepresentationModel;

import javax.validation.constraints.NotBlank;

public class CategoryRequest {

    @JsonIgnore
    private final ProductCategory category = new ProductCategory();

    public void setId(Long id) { category.setId(id); }

    public Long getId() { return category.getId(); }

    public void setName(String name) { category.setName(name); }

    @NotBlank(message = "Category name can't be blank")
    public String getName() { return category.getName(); }

    public ProductCategory getCategory() { return category; }

}
