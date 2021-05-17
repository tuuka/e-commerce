package net.tuuka.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.tuuka.ecommerce.controller.assembler.ProductCategoryModelAssembler;
import net.tuuka.ecommerce.controller.assembler.ProductModelAssembler;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.service.ProductService;
import net.tuuka.ecommerce.util.FakeProductGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(controllers = {ProductRestController.class})
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ProductService productService;

    @MockBean
    ProductModelAssembler productAssembler;

    @MockBean
    ProductCategoryModelAssembler categoryAssembler;

    @Value("${app.api.path}/products")
    private String apiUrl;

    List<Product> products;

    @BeforeEach
    void setUp() {

        products = FakeProductGenerator.getNewFakeProductList(3, 3);

        given(productAssembler.toModel(any())).will(
                (InvocationOnMock invocation) -> {
                    Product product = invocation.getArgument(0);
                    return EntityModel.of(product).add(linkTo(methodOn(ProductRestController.class)
                            .getProductById(product.getId())).withSelfRel());
                });
        given(productAssembler.toCollectionModel(any())).will(
                (InvocationOnMock invocation) -> {
                    List<Product> productList = invocation.getArgument(0);
                    return productList.stream().map(EntityModel::of)
                            .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
                }
        );
    }

    @AfterEach
    void tearDown() {
        products = null;
    }

    @Test
    void whenGetProductsMapping_shouldReturnProductList() throws Exception {

        given(productService.getAll()).willReturn(products);

        mockMvc.perform(get(apiUrl)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$._embedded.products", hasSize(products.size())))
                .andDo(MockMvcResultHandlers.print());

        then(productService).should().getAll();
        then(productAssembler).should().toCollectionModel(any());

    }

    @Test
    void givenProductId_whenGetProductsIdMapping_shouldReturnProduct() throws Exception {

        products.get(0).setId(1L);
        given(productService.getById(anyLong())).willReturn(products.get(0));

        mockMvc.perform(get(apiUrl + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("name_00"))
                .andDo(MockMvcResultHandlers.print());

        then(productService).should().getById(eq(1L));
        then(productAssembler).should().toModel(any());

    }

    @Test
    void givenNonExistingProductId_whenGetProductsIdMapping_shouldReturnNotFoundErrorEntity() throws Exception {

        given(productService.getById(anyLong()))
                .willThrow(new EntityNotFoundException("not found"));

        mockMvc.perform(get(apiUrl + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.error").value("not found"))
                .andDo(MockMvcResultHandlers.print());

        then(productService).should().getById(eq(1L));

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
