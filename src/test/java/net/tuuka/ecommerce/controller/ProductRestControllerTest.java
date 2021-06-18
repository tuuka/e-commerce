package net.tuuka.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.tuuka.ecommerce.controller.util.ProductCategoryModelAssembler;
import net.tuuka.ecommerce.controller.util.ProductModelAssembler;
import net.tuuka.ecommerce.model.Product;
import net.tuuka.ecommerce.exception.aspect.ProductControllerAdvice;
import net.tuuka.ecommerce.service.ProductService;
import net.tuuka.ecommerce.util.FakeProductGenerator;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@ActiveProfiles("test")
//@WebMvcTest(controllers = {ProductRestController.class})
class ProductRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    ProductService productService;

    @Spy
    ProductModelAssembler productAssembler;

    @Spy
    ProductCategoryModelAssembler categoryAssembler;

    @Spy
    PagedResourcesAssembler<Product> productPagedAssembler =
            new PagedResourcesAssembler<>(
                    new HateoasPageableHandlerMethodArgumentResolver(),
                    null);

    @InjectMocks
    ProductRestController controller;

    //    @Value("${app.api.path}/products")
    private final String apiUrl = "/api/products";

    List<Product> products;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ProductControllerAdvice())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        products = FakeProductGenerator.getNewFakeProductList(3, 3);

    }

    @AfterEach
    void tearDown() {
        products = null;
    }

    @Test
    void whenGetProductsMapping_shouldReturnProductList() throws Exception {

        given(productService.findAll(any())).willReturn(new PageImpl<>(products));

        mockMvc.perform(get(apiUrl)
                .contentType(MediaType.valueOf("application/hal+json")))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.content.*", hasSize(products.size())))
                .andExpect(jsonPath("$.links[0].href", Matchers.containsString("localhost")))
                .andDo(MockMvcResultHandlers.print());

        then(productService).should().findAll(any());

    }

    @Test
    void givenProductId_whenGetProductsIdMapping_shouldReturnProduct() throws Exception {

        products.get(0).setId(1L);
        given(productService.findById(anyLong())).willReturn(products.get(0));

        mockMvc.perform(get(apiUrl + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name_00"))
                .andExpect(jsonPath("$.links[0].href", Matchers.containsString("localhost")))
                .andDo(MockMvcResultHandlers.print());

        then(productService).should().findById(eq(1L));
        then(productAssembler).should().toModel(any());

    }

    @Test
    void givenNonExistingProductId_whenGetProductsIdMapping_shouldReturnNotFoundErrorEntity() throws Exception {

        given(productService.findById(anyLong()))
                .willThrow(new EntityNotFoundException("not found"));

        mockMvc.perform(get(apiUrl + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.message").value("not found"))
                .andDo(MockMvcResultHandlers.print());

        then(productService).should().findById(eq(1L));

    }

    @Test
    void givenProduct_whenSaveProductMapping_shouldReturnSavedProduct() throws Exception {

        given(productService.save(any()))
                .will((InvocationOnMock invocation) -> {
                    Product tempProduct = invocation.getArgument(0);
                    tempProduct.setId(1L);
                    return tempProduct;
                });
        String jsonProduct = new ObjectMapper().writeValueAsString(products.get(0));

        mockMvc.perform(post(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonProduct))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(MockMvcResultHandlers.print());

        then(productService).should().save(isA(Product.class));

    }

    @Test
    void givenProduct_whenUpdateProductMapping_shouldReturnUpdatedProduct() throws Exception {

        products.get(0).setId(1L);

        given(productService.update(any())).willReturn(products.get(0));
        String jsonProduct = new ObjectMapper().writeValueAsString(products.get(0));

        mockMvc.perform(put(apiUrl + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonProduct))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(MockMvcResultHandlers.print());

        then(productService).should().update(isA(Product.class));

    }

    @Test
    void givenProduct_whenDeleteProductMapping_shouldReturnDeletedProduct() throws Exception {

        products.get(0).setId(1L);
        given(productService.deleteById(anyLong())).willReturn(products.get(0));

        mockMvc.perform(delete(apiUrl + "/1")
                .contentType(MediaType.valueOf("application/hal+json")))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(MockMvcResultHandlers.print());

        then(productService).should().deleteById(eq(1L));

    }

}
