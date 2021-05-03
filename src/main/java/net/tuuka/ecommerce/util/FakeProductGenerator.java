package net.tuuka.ecommerce.util;

import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FakeProductGenerator {

    private static final int NUMBER_OF_CATEGORIES = 5;
    private static final int PRODUCTS_PER_CATEGORY = 3;

    public static List<Product> getFakeProductList(){
        return FakeProductsAndCategoryFactory.getProducts();
    }

    public static List<ProductCategory> getFakeProductCategoriesList(){
        return FakeProductsAndCategoryFactory.getProductCategories();
    }


    // To be sure that products and productsCategories lists are singletons and thread safe
    // initialize them through 'java class lazy initialization'

    private static class FakeProductsAndCategoryFactory {
        private static final List<ProductCategory> productCategories = new ArrayList<>();
        private static final List<Product> products = new ArrayList<>();
        private static final Random random = new Random();

        // basically for testing purposes, don't change 'naming' strategy
        // '_' may be using to split and get ids
        static {
            Product product;
            for (int i = 0; i < NUMBER_OF_CATEGORIES; i++) {
                productCategories.add(
                        new ProductCategory("cat_" + i)
                );
                for (int j = 0; j < PRODUCTS_PER_CATEGORY; j++) {
                    product = new Product(
                            "sku_" + i + j,
                            "name_" + i + j,
                            "description_" + i + j,
                            random.nextInt(100) +
                                    0.01 * random.nextInt(100),
                            "assets/images/" +
                                    productCategories.get(i).getName() +
                                    "/image_" + i + j + ".jpg",
                            true,
                            random.nextInt(100));
                    product.setCategory(productCategories.get(i));
                    products.add(product);
                }
            }
        }

        private static List<Product> getProducts(){
            return products;
        }

        private static List<ProductCategory> getProductCategories(){
            return productCategories;
        }
    }

}
