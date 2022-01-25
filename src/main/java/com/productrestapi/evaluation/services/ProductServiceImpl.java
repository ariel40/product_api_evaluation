package com.productrestapi.evaluation.services;

import com.productrestapi.evaluation.exceptions.ProductNotFoundException;
import com.productrestapi.evaluation.model.Price;
import com.productrestapi.evaluation.model.Product;
import com.productrestapi.evaluation.repositories.ProductRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Get all available products.
     * @param pageable the object shows how the data should retrieve.
     * @return {@link List<Product>} with products
     */
    @Override
    public List<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).getContent();
    }

    /**
     * Get one existing price or throws an exception.
     * @param id the unique product id.
     * @return {@link Product} with the product.
     */
    @Override
    public Product getProduct(long id) {
        return this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    /**
     * Create a new product.
     * @param product the object contains product information
     * @return {@link Product} the created product.
     */
    @Override
    @PreAuthorize("hasAuthority('ROLE_PRODUCT_CREATORS')")
    public Product createProduct(Product product) {
        product.setCreationDate(Instant.now());

        if(product.getPrices().size() > 0){
            product.getPrices().forEach(this::fillPrices);
        }
        return this.productRepository.save(product);
    }



    /**
     * Update an existing product or throws an exception.
     * @param id the unique product id.
     * @param product the object contains the data to be updated.
     * @return {@link Product} the updated object.
     */
    @Override
    @PreAuthorize("hasAuthority('ROLE_PRODUCT_MANAGERS')")
    public Product updateProduct(long id, Product product) {
        this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return this.productRepository.save(product);
    }

    /**
     * Delete an existing product or throws an exception.
     * @param id the unique product id.
     */
    @Override
    @PreAuthorize("hasAuthority('ROLE_PRODUCT_MANAGERS')")
    public void deleteProduct(long id) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        this.productRepository.delete(product);
    }

    private Price fillPrices(Price price) {
        price.getProduct();
        return price;
    }

}
