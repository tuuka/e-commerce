package net.tuuka.ecommerce.controller.v1;

import lombok.RequiredArgsConstructor;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.path}" + "/v1/products")
public class ProductRestControllerV1 {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable long id) {
        return productService.getById(id);
    }

    @GetMapping("/search")
    public List<Product> getProductBySku(@RequestParam(name = "sku", defaultValue = "") String sku,
                                         @RequestParam(value = "name", defaultValue = "") String name) {
        return productService.findAllBySkuOrName(sku, name);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Product saveProduct(@RequestBody Product product) {
        return productService.save(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable long id, @RequestBody Product product) {
        product.setId(id);
        return productService.update(product);
    }

    @DeleteMapping("/{id}")
    public Product deleteProductById(@PathVariable long id) {
        return productService.deleteById(id);
    }

}
