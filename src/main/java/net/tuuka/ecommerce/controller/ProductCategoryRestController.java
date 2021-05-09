package net.tuuka.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.path}" + "/v1/product_categories")
public class ProductCategoryRestController {
}
