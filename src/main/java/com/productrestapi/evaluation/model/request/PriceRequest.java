package com.productrestapi.evaluation.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Accessors(chain = true)
public class PriceRequest {

    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private double price;

    @NotNull
    private String status;
}
