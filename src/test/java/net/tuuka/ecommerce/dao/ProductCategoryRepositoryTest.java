package net.tuuka.ecommerce.dao;

import net.tuuka.ecommerce.entity.ProductCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
    Category class has not null field:
        private String name;                // not null
*/

@DataJpaTest
@ActiveProfiles("test")
class ProductCategoryRepositoryTest {

    @Autowired
    ProductCategoryRepository productCategoryRepository;

    @Test
    void givenCategoryName_whenFindByName_ShouldReturnOptional() {

        // given
        String categoryName = "Some fancy name";
        ProductCategory productCategory = new ProductCategory(categoryName);
        productCategoryRepository.save(productCategory);
        productCategoryRepository.flush();

        // when
        Optional<ProductCategory> fetchedCategory = productCategoryRepository.findByName(categoryName);

        // then
        assertTrue(fetchedCategory.isPresent());
        assertNotNull(fetchedCategory.get().getId());
        assertEquals(categoryName, fetchedCategory.get().getName());
    }
}
