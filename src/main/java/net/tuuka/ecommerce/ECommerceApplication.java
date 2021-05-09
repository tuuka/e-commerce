package net.tuuka.ecommerce;

import net.tuuka.ecommerce.dao.ProductCategoryRepository;
import net.tuuka.ecommerce.dao.ProductRepository;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.util.FakeProductGenerator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class ECommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApplication.class, args);
    }

    //    @Bean
    public CommandLineRunner initDB(ProductRepository productRepository, ProductCategoryRepository categoryRepository) {
        return (r) -> {
            productRepository.deleteAll();
            categoryRepository.deleteAll();
            List<Product> products = FakeProductGenerator.getNewFakeProductList(5, 3);
            productRepository.saveAll(products);
        };
    }

}
