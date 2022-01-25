package com.productrestapi.evaluation.services;

import com.productrestapi.evaluation.model.Price;
import com.productrestapi.evaluation.model.request.PriceRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {

    List<Price> getAllPrices(Pageable pageable);

    Price getPrice(Long id);

    Price createPrice(PriceRequest price);

    void deletePrice(Long id);

    Price updatePrice(Long id, Price price);
}
