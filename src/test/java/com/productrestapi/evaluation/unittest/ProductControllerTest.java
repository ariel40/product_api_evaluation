package com.productrestapi.evaluation.unittest;

import com.productrestapi.evaluation.controllers.ProductController;
import com.productrestapi.evaluation.exceptions.ProductNotFoundException;
import com.productrestapi.evaluation.model.Product;
import com.productrestapi.evaluation.services.ProductService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import static com.productrestapi.evaluation.utils.JsonUtils.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @MockBean
    ProductService productService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/product success")
    void testGetProductsSuccess() throws Exception {
        Product product1 = this.mockProduct().setId(1L);
        Product product2 = this.mockProduct().setId(2L);
        Pageable pageable = PageRequest.of(0, 15, Sort.by("name").ascending());
        ArrayList<Product> arrayList = Lists.newArrayList(product1, product2);
        // Setup our mocked service
        when(productService.getAllProducts(pageable)).thenReturn(arrayList);

        // Execute the GET request
        mockMvc.perform(get("/api/v1/product/")
                        .param("page", "0")
                        .param("size", "15")
                        .param("sortBy", "name"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Leche Evaporada Carnation")))
                .andExpect(jsonPath("$[0].status", is("A")))
                .andExpect(jsonPath("$[0].prices", hasSize(0)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].description", is("Leche Evaporada Carnation")))
                .andExpect(jsonPath("$[1].status", is("A")))
                .andExpect(jsonPath("$[1].prices", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/v1/product/1")
    void testGetProductSuccess() throws Exception {
        // Setup our mocked service
        Instant date = Instant.now();
        Product product = mockProduct().setId(1L).setCreationDate(date).setModificationDate(date);
        when(productService.getProduct(1L)).thenReturn(product);

        // Execute the GET request
        mockMvc.perform(get("/api/v1/product/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Leche Evaporada Carnation")))
                .andExpect(jsonPath("$.creationDate", is(date.toString())))
                .andExpect(jsonPath("$.modificationDate", is(date.toString())))
                .andExpect(jsonPath("$.status", is("A")))
                .andExpect(jsonPath("$.prices", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/v1/product/1 - Not Found")
    void testGetProductByIdNotFound() throws Exception {
        // Setup our mocked service
        when(productService.getProduct(1L)).thenThrow(ProductNotFoundException.class);

        // Execute the GET request
        mockMvc.perform(get("/api/v1/product/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/product")
    void testCreateProduct() throws Exception {
        Instant date = Instant.now();
        Product productToPost = this.mockProduct();
        Product productToReturn = this.mockProduct().setId(1L).setCreationDate(date);

        // Setup our mocked service
        when(productService.createProduct(any())).thenReturn(productToReturn);

        // Execute the POST request
        mockMvc.perform(post("/api/v1/product/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(productToPost)))
                .andDo(print())

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Leche Evaporada Carnation")))
                .andExpect(jsonPath("$.creationDate", is(date.toString())))
                .andExpect(jsonPath("$.status", is("A")))
                .andExpect(jsonPath("$.prices", hasSize(0)));

    }

    @Test
    @DisplayName("POST /api/v1/product")
    void testCreateProductBadRequest() throws Exception {
        Product productToPost = this.mockProduct();
        productToPost.setDescription(null);

        // Execute the POST request
        mockMvc.perform(post("/api/v1/product/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(productToPost)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/product")
    void testUpdateProduct() throws Exception {
        Instant date = Instant.now();
        Instant yesterday = Instant.now().minus(24, ChronoUnit.HOURS);
        Product productToUpdate = this.mockProduct().setDescription("Updating Description").setCreationDate(yesterday);
        Product productToReturnFindBy = this.mockProduct().setId(1L).setCreationDate(yesterday);
        Product productToReturnSave = this.mockProduct().setId(1L).setDescription("Updating Description").setModificationDate(date).setCreationDate(yesterday);
        when(productService.getProduct(1L)).thenReturn(productToReturnFindBy);
        when(productService.updateProduct(1L, productToUpdate)).thenReturn(productToReturnSave);

        // Execute the POST request
        mockMvc.perform(post("/api/v1/product/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(productToUpdate)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Updating Description")))
                .andExpect(jsonPath("$.creationDate", is(productToReturnFindBy.getCreationDate().toString())))
                .andExpect(jsonPath("$.modificationDate", is(date.toString())))
                .andExpect(jsonPath("$.status", is(productToReturnFindBy.getStatus())))
                .andExpect(jsonPath("$.prices", hasSize(0)));

    }

    @Test
    @DisplayName("POST /api/v1/product/1 - Not Found")
    void testUpdateProductNotFound() throws Exception {
        Instant yesterday = Instant.now().minus(24, ChronoUnit.HOURS);
        Product productToUpdate = this.mockProduct().setDescription("Updating Description").setId(1L).setCreationDate(yesterday);

        // Setup our mocked service
        when(productService.updateProduct(1L, productToUpdate)).thenThrow(ProductNotFoundException.class);

        // Execute the POST request
        mockMvc.perform(post("/api/v1/product/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(productToUpdate)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/product/1")
    void testDeleteProduct() throws Exception {
        // Setup our mocked service
        Product productToReturnFindBy = this.mockProduct().setId(1L);
        doNothing().when(productService).deleteProduct(1L);

        // Execute the DELETE request
        mockMvc.perform(delete("/api/v1/product/{id}", 1L))
                .andExpect(status().isOk());
    }

    private Product mockProduct() {
        return new Product()
                .setDescription("Leche Evaporada Carnation")
                .setStatus("A");
    }
}
