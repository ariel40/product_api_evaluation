package com.productrestapi.evaluation.unittest;

import com.productrestapi.evaluation.controllers.PriceController;
import com.productrestapi.evaluation.exceptions.PriceNotFoundException;
import com.productrestapi.evaluation.model.Price;
import com.productrestapi.evaluation.model.Product;
import com.productrestapi.evaluation.model.request.PriceRequest;
import com.productrestapi.evaluation.services.PriceService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PriceController.class)
class PriceControllerTest {

    @MockBean
    PriceService priceService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/price/ success")
    void getAllPricesSuccess() throws Exception {
        Price price1 = this.mockPrice().setId(1L);
        Price price2 = this.mockPrice().setId(2L);
        Pageable pageable = PageRequest.of(0, 15, Sort.by("name").ascending());
        ArrayList<Price> arrayList = Lists.newArrayList(price1, price2);
        // Setup our mocked service
        when(priceService.getAllPrices(pageable)).thenReturn(arrayList);

        // Execute the GET request
        mockMvc.perform(get("/api/v1/price/")
                        .param("page", "0")
                        .param("size", "15")
                        .param("sortBy", "name"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].price", is(9.99)))
                .andExpect(jsonPath("$[0].status", is("A")))
                .andExpect(jsonPath("$[0].product.id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].price", is(9.99)))
                .andExpect(jsonPath("$[1].status", is("A")))
                .andExpect(jsonPath("$[1].product.id", is(1)));
    }

    @Test
    @DisplayName("GET /api/v1/price/1 success")
    void testGetPriceSuccess() throws Exception {
        // Setup our mocked service
        Price price = this.mockPrice().setId(1L);
        when(priceService.getPrice(1L)).thenReturn(price);

        // Execute the GET request
        mockMvc.perform(get("/api/v1/price/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.price", is(9.99)))
                .andExpect(jsonPath("$.status", is("A")))
                .andExpect(jsonPath("$.product.id", is(1)));
    }

    @Test
    @DisplayName("GET /api/v1/price/1 success")
    void testGetPriceNotFound() throws Exception {
        // Setup our mocked service
        when(priceService.getPrice(1L)).thenThrow(PriceNotFoundException.class);

        // Execute the GET request
        mockMvc.perform(get("/api/v1/price/{id}", 1L))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/price/1 201-created")
    void testCreatePrice() throws Exception {
        Instant now = Instant.now();
        PriceRequest priceToPost = mockPriceRequest();
        Price priceToReturn = mockPrice().setId(1L).setCreationDate(now);

        // Setup our mock service
        when(priceService.createPrice(priceToPost)).thenReturn(priceToReturn);

        // Execute the POST request
        mockMvc.perform(post("/api/v1/price/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(priceToPost)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.price", is(9.99)))
                .andExpect(jsonPath("$.status", is("A")))
                .andExpect(jsonPath("$.product.id", is(1)));
    }

    @Test
    @DisplayName("GET /api/v1/price/1 400-badRequest")
    void testCreatePriceBadRequest() throws Exception {
        Price priceToPost = mockPrice();
        priceToPost.setProduct(null);

        // Execute the POST request
        mockMvc.perform(post("/api/v1/price/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(priceToPost)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/price/1")
    void testUpdatePrice() throws Exception {
        // Setup our mocked service
        Instant now = Instant.now();
        Instant yesterday = Instant.now().minus(24, ChronoUnit.HOURS);
        double updatedPrice = 12.12;
        Price priceToUpdate = this.mockPrice().setId(1L).setPrice(updatedPrice);
        Price priceToReturnFindBy = this.mockPrice().setId(1L).setCreationDate(yesterday);
        Price priceToReturnSave = this.mockPrice().setId(1L).setModificationDate(now);

        when(priceService.getPrice(1L)).thenReturn(priceToReturnFindBy);
        when(priceService.updatePrice(1L, priceToUpdate)).thenReturn(priceToReturnSave);

        // Execute the POST request
        mockMvc.perform(post("/api/v1/price/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(priceToUpdate)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/price/1")
    void testUpdatePriceNotFound() throws Exception {
        // Setup our mocked service
        Price priceToUpdate = this.mockPrice().setId(1L);
        when(priceService.updatePrice(1L, priceToUpdate)).thenThrow(PriceNotFoundException.class);

        // Execute the POST request
        mockMvc.perform(post("/api/v1/price/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(priceToUpdate)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/price/1")
    void testDeletePrice() throws Exception {
        // Setup our mocked service
        doNothing().when(priceService).deletePrice(1L);

        // Execute the DELETE request
        mockMvc.perform(delete("/api/v1/price/{id}", 1L))
                .andExpect(status().isOk());
    }

    private Price mockPrice() {
        Product product = new Product()
                .setId(1L)
                .setCreationDate(Instant.now())
                .setModificationDate(Instant.now())
                .setDescription("Leche Evaporada Carnation")
                .setStatus("A");
        return new Price()
                .setPrice(9.99)
                .setStatus("A")
                .setProduct(product);
    }
    private PriceRequest mockPriceRequest() {
        return new PriceRequest()
                .setPrice(9.99)
                .setStatus("A")
                .setProductId(1L);
    }
}
