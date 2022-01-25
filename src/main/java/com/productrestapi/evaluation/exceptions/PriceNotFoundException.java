package com.productrestapi.evaluation.exceptions;


public class PriceNotFoundException extends RuntimeException {

    public PriceNotFoundException(Long id) {
        super("Price id not found: " + id);
    }
}
