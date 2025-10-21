package com.github.souzafcharles.api.endpoint.cartproduct.model.dto;

import java.io.Serializable;

public record ProductSalesDTO(
        String productId,
        String title,
        Integer totalSold
) implements Serializable { }