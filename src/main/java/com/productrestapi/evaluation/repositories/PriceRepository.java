package com.productrestapi.evaluation.repositories;

import com.productrestapi.evaluation.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {
}
