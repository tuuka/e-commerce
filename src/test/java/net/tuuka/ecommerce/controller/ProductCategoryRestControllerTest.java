package net.tuuka.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.tuuka.ecommerce.controller.assembler.ProductCategoryModelAssembler;
import net.tuuka.ecommerce.controller.assembler.ProductModelAssembler;
import net.tuuka.ecommerce.entity.Product;
import net.tuuka.ecommerce.entity.ProductCategory;
import net.tuuka.ecommerce.service.ProductCategoryService;
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
import java.util.Arrays;
import java.util.LinkedList;
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

@WebMvcTest(controllers = {ProductCategoryRestController.class})
@ActiveProfiles("test")
class ProductCategoryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ProductCategoryService categoryService;

    @MockBean
    ProductCategoryModelAssembler categoryAssembler;

    @MockBean
    ProductModelAssembler productAssembler;

    @Value("${app.api.path}/product_categories")
    private String apiUrl;

    List<ProductCategory> categories;
    Product product1, product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(
                "sku1",
                "name1",
                "desc1",
                1.,
                "url1",
                true,
                10);
        product2 = new Product(
                "sku2",
                "name2",
                "desc2",
                2.,
                "url2",
                true,
                20);
        categories = new LinkedList<>(Arrays.asList(
                new ProductCategory("cat1", Arrays.asList(product1, product2)),
                new ProductCategory("cat2")));


        given(categoryAssembler.toModel(any())).will(
                (InvocationOnMock invocation) -> {
                    ProductCategory category = invocation.getArgument(0);
                    return EntityModel.of(category).add(linkTo(methodOn(ProductCategoryRestController.class)
                            .getCategoryById(category.getId())).withSelfRel());
                });
        given(categoryAssembler.toCollectionModel(any())).will(
                (InvocationOnMock invocation) -> {
                    List<ProductCategory> categoryList = invocation.getArgument(0);
                    return categoryList.stream().map(EntityModel::of)
                            .collect(Collectors.collectingAndThen(Collectors.toList(), CollectionModel::of));
                }
        );

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
        categories = null;
        product1 = product2 = null;
    }

    @Test
    void whenGetCategoriesMapping_shouldReturnCategoryList() throws Exception {

        given(categoryService.getAll()).willReturn(categories);

        mockMvc.perform(get(apiUrl)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$._embedded.categories.*", hasSize(categories.size())))
                .andDo(MockMvcResultHandlers.print());

        then(categoryService).should().getAll();

    }

    @Test
    void givenCategoryId_whenGetCategoryByIdMapping_shouldReturnCategory() throws Exception {

        ProductCategory category = categories.get(0);
        category.setId(1L);
        given(categoryService.getById(anyLong())).willReturn(category);

        mockMvc.perform(get(apiUrl + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(category.getName()))
                .andDo(MockMvcResultHandlers.print());

        then(categoryService).should().getById(1L);

    }

    @Test
    void givenNonExistingCategoryId_whenGetCategoriesByIdMapping_shouldReturnNotFoundErrorEntity() throws Exception {

        given(categoryService.getById(anyLong()))
                .willThrow(new EntityNotFoundException("not found"));

        mockMvc.perform(get(apiUrl + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.error").value("not found"))
                .andDo(MockMvcResultHandlers.print());

        then(categoryService).should().getById(eq(1L));

    }

    @Test
    void givenCategory_whenSaveCategoryMapping_shouldReturnSavedCategory() throws Exception {

        given(categoryService.save(any()))
                .will((InvocationOnMock invocation) -> {
                    ProductCategory tempCategory = invocation.getArgument(0);
                    tempCategory.setId(1L);
                    return tempCategory;
                });
        String jsonCategory = new ObjectMapper().writeValueAsString(categories.get(0));

        mockMvc.perform(post(apiUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCategory))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(MockMvcResultHandlers.print());

        then(categoryService).should().save(isA(ProductCategory.class));

    }

    @Test
    void givenCategory_whenUpdateCategoryMapping_shouldReturnUpdatedCategory() throws Exception {

        categories.get(0).setId(1L);

        given(categoryService.update(any())).willReturn(categories.get(0));
        String jsonCategory = new ObjectMapper().writeValueAsString(categories.get(0));

        mockMvc.perform(put(apiUrl + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCategory))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(MockMvcResultHandlers.print());

        then(categoryService).should().update(isA(ProductCategory.class));

    }

    @Test
    void givenCategory_whenDeleteCategoryMapping_shouldReturnDeletedCategory() throws Exception {

        categories.get(0).setId(1L);
        given(categoryService.deleteById(anyLong())).willReturn(categories.get(0));

        mockMvc.perform(delete(apiUrl + "/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.id").value(1L))
                .andDo(MockMvcResultHandlers.print());

        then(categoryService).should().deleteById(eq(1L));

    }

    @Test
    void givenNonExistingCategoryId_whenUpdateCategoryMapping_shouldThrowException() throws Exception {

        categories.get(0).setId(1L);

        given(categoryService.update(any()))
                .willThrow(new EntityNotFoundException("not found"));
        String jsonCategory = new ObjectMapper().writeValueAsString(categories.get(0));

        mockMvc.perform(put(apiUrl + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonCategory))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.valueOf("application/hal+json")))
                .andExpect(jsonPath("$.error").value("not found"))
                .andDo(MockMvcResultHandlers.print());

        then(categoryService).should().update(isA(ProductCategory.class));

    }

}
