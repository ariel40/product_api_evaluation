package com.productrestapi.evaluation.repositories;

import com.productrestapi.evaluation.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
