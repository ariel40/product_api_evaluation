package com.productrestapi.evaluation.model;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "PRODUCT")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "CREATION_DATE")
    private Instant creationDate;

    @Column(name = "MODIFICATION_DATE")
    private Instant modificationDate;

    @Column(name = "PRODUCT_STATUS")
    private String status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<Price> prices;
}
