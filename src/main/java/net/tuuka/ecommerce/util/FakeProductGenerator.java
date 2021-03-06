package net.tuuka.ecommerce.util;

import net.tuuka.ecommerce.model.product.Product;
import net.tuuka.ecommerce.model.product.ProductCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class FakeProductGenerator {

    public static void setIdsToGivenProducts(List<Product> productList) {
        IntStream.range(0, productList.size()).forEach(i ->
        {
            productList.get(i).setId((long) i + 1);
            ProductCategory cat = productList.get(i).getCategory();
            cat.setId(Long.parseLong(cat.getName().split("_")[1]) + 1);
        });
    }

    public static List<Product> getNewFakeProductList(int numOfCats, int productsPerCat) {
        List<ProductCategory> productCategories = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        Random random = new Random();

        // basically for testing purposes, don't change 'naming' strategy
        // '_' may be using to split and get ids
        Product product;
        ProductCategory productCategory;
        int counter = 1;
        for (int i = 0; i < numOfCats; i++) {
            productCategory = new ProductCategory("cat_" + i);
            productCategory.setProducts(new ArrayList<>());
            productCategories.add(productCategory);
            for (int j = 0; j < productsPerCat; j++) {
                product = new Product(
                        "sku_" + i + j,
                        "name_" + i + j,
                        "description_" + i + j,
                        random.nextInt(100) +
                                0.01 * random.nextInt(100),
                        "images/products/sku_" + (counter < 10 ? "0" + counter++ : counter++) + ".jpg",
                        true,
                        random.nextInt(100));
                product.setCategory(productCategories.get(i));
//                productCategories.get(i).getProducts().add(product);
                products.add(product);
            }
        }
        return products;
    }
}
