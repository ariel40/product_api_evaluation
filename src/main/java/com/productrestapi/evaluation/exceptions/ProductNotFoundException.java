package com.productrestapi.evaluation.exceptions;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product id not found: " + id);
    }
}
