package com.productrestapi.evaluation.services;

import com.productrestapi.evaluation.exceptions.PriceNotFoundException;
import com.productrestapi.evaluation.exceptions.ProductNotFoundException;
import com.productrestapi.evaluation.model.Price;
import com.productrestapi.evaluation.model.Product;
import com.productrestapi.evaluation.model.request.PriceRequest;
import com.productrestapi.evaluation.repositories.PriceRepository;
import com.productrestapi.evaluation.repositories.ProductRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final ProductRepository productRepository;

    public PriceServiceImpl(PriceRepository priceRepository, ProductRepository productRepository) {
        this.priceRepository = priceRepository;
        this.productRepository = productRepository;
    }

    /**
     * Get all available prices
     * @param pageable the object shows how the data should retrieve.
     * @return {@link List<Price>} with the prices
     */
    @Override
    public List<Price> getAllPrices(Pageable pageable) {
        return priceRepository.findAll(pageable).getContent();
    }

    /**
     * Get one existing price or throws an exception.
     * @param id the unique price id.
     * @return {@link Price} with the price.
     */
    @Override
    public Price getPrice(Long id) {
        return this.priceRepository.findById(id)
                .orElseThrow(() -> new PriceNotFoundException(id));
    }

    /**
     * Create a new price
     * @param price the object contains price information.
     * @return {@link Price} the created object.
     */
    @Override
    @PreAuthorize("hasAuthority('ROLE_PRODUCT_PRICING')")
    public Price createPrice(PriceRequest price) {
        Price lastElement = null;
        Product product = this.productRepository.findById(price.getProductId())
                            .orElseThrow(() -> new ProductNotFoundException(price.getProductId()));
        product.getPrices().add(this.getPriceObj(price));
        Set<Price> prices = this.productRepository.save(product).getPrices();

        Iterator<Price> iterator = prices.iterator();
        while(iterator.hasNext()){
            lastElement = iterator.next();
        }
        return lastElement;
    }

    /**
     * Delete an existing price or throws an exception.
     * @param id the unique price id.
     */
    @Override
    @PreAuthorize("hasAuthority('ROLE_PRODUCT_PRICING')")
    public void deletePrice(Long id) {
        Price price = this.priceRepository.findById(id)
                .orElseThrow(() -> new PriceNotFoundException(id));
        this.priceRepository.delete(price);
    }

    /**
     * Update an existing price or throws an exception.
     * @param id the unique price id.
     * @param price the object contains the data to be updated.
     * @return {@link Price} the object already updated.
     */
    @Override
    @PreAuthorize("hasAuthority('ROLE_PRODUCT_PRICING')")
    public Price updatePrice(Long id, Price price) {
        this.priceRepository.findById(id)
                .orElseThrow(() -> new PriceNotFoundException(id));
        return this.priceRepository.save(price);
    }

    private Price getPriceObj(PriceRequest priceRequest){
        Price price = new Price();
        price.setCreationDate(Instant.now());
        price.setStatus(priceRequest.getStatus());
        price.setPrice(priceRequest.getPrice());

        return price;
    }
}
