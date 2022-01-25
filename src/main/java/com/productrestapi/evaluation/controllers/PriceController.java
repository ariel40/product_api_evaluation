package com.productrestapi.evaluation.controllers;

import com.productrestapi.evaluation.model.Price;
import com.productrestapi.evaluation.model.request.PriceRequest;
import com.productrestapi.evaluation.services.PriceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/price")
public class PriceController {

    final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping
    public ResponseEntity<List<Price>> getAllPrices(@RequestParam(name = "index", defaultValue = "0") int index,
                                                    @RequestParam(value = "size", defaultValue = "15") int size,
                                                    @RequestParam(value = "sortBy", defaultValue = "id") String name) {
        Pageable pageable = PageRequest.of(index, size, Sort.by(name).ascending());
        return ResponseEntity.ok(this.priceService.getAllPrices(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Price> getPrice(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.priceService.getPrice(id));
    }

    @PostMapping
    public ResponseEntity<Price> createPrice(@RequestBody @Valid PriceRequest price) {
        return new ResponseEntity<>(this.priceService.createPrice(price), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePrice(@PathVariable("id") Long id) {
        priceService.deletePrice(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}")
    public ResponseEntity<Price> updatePrice(@PathVariable("id") Long id, @RequestBody @Valid Price price) {
        return ResponseEntity.ok(this.priceService.updatePrice(id, price));
    }
}
