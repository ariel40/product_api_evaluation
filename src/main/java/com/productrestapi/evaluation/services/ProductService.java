package com.productrestapi.evaluation.services;

import com.productrestapi.evaluation.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ProductService {

    List<Product> getAllProducts(Pageable pageable);

    Product getProduct(long id);

    Product createProduct(Product product);

    Product updateProduct(long id, Product product);

    void deleteProduct(long id);
}
