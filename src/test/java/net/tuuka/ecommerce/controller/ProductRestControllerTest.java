package net.tuuka.ecommerce.controller;

import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.exception.ProductNotFoundException;
import net.tuuka.ecommerce.service.ProductService;
import net.tuuka.ecommerce.util.FakeProductGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {ProductRestController.class})
@ActiveProfiles("test")
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ProductService productService;

    @Value("${app.api.path}" + "${app.api.active_version}" + "/products")
    private String apiUrl;

    List<Product> products;

    @BeforeEach
    void setUp() {
        products = FakeProductGenerator.getNewFakeProductList(3, 3);
    }

    @AfterEach
    void tearDown() {
        products = null;
    }

    @Test
    void whenGetProductsMapping_shouldReturnProductList() throws Exception {

        given(productService.getAllProducts()).willReturn(products);

        mockMvc.perform(get(apiUrl)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.*", hasSize(products.size())))
                .andDo(MockMvcResultHandlers.print());

        then(productService).should().getAllProducts();

    }

    @Test
    void givenProductId_whenGetProductsIdMapping_shouldReturnProductList() throws Exception {

        given(productService.getProductById(anyLong())).willReturn(products.get(0));

        mockMvc.perform(get(apiUrl + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.*", hasSize(products.size())))
                .andDo(MockMvcResultHandlers.print());

        then(productService).should().getProductById(eq(1L));

    }

    @Test
    void givenNonExistingProductId_whenAnyMethodAnyMapping_shouldReturnNotFoundErrorEntity() throws Exception {

        given(productService.getProductById(anyLong()))
                .willThrow(new ProductNotFoundException(""));

        mockMvc.perform(get(apiUrl + "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.*", hasSize(products.size())))
                .andDo(MockMvcResultHandlers.print());

        then(productService).should().getProductById(eq(1L));

    }

}
