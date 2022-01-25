package com.productrestapi.evaluation.model;

import lombok.Data;
import lombok.experimental.Accessors;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "PRICE")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @Column(name = "CREATION_DATE")
    private Instant creationDate;

    @Column(name = "MODIFICATION_DATE")
    private Instant modificationDate;

    @NotNull
    @Column(name = "AMOUNT")
    private double  price;

    @Column(name = "STATUS")
    private String status;
}
