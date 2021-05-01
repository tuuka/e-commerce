package net.tuuka.ecommerce;

import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.dao.ProductRepository;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner initDB(ProductRepository productRepository, ProductCategoryRepository categoryRepository){
//        return (r)->{
//            Product product;
//            List<Product> products = new ArrayList<>();
//            List<ProductCategory> categories = new ArrayList<>();
//            for (int i = 0; i < 5; i++) {
//                categories.add(new ProductCategory("cat_" + i));
//                product = new Product("sku_" + (i * 2), "name_" + (i * 2), null,
//                        null, null, true, null);
//                product.setCategory(categories.get(i));
//                products.add(product);
//                product = new Product("sku_" + (i * 2 + 1), "name_" + (i * 2 + 1), null,
//                        null, null, true, null);
//                product.setCategory(categories.get(i));
//                products.add(product);
//            }
//            categories.forEach(categoryRepository::save);
//            products.forEach(productRepository::save);
//        };
//    }

}
