package com.github.souzafcharles.api.endpoint.product.model.dto;

import com.github.souzafcharles.api.endpoint.product.model.entity.Product;
import java.io.Serializable;

public record ProductResponseDTO(
        String id,
        String title,
        Double price,
        String description,
        String category,
        String image
) implements Serializable {
    public ProductResponseDTO(Product product) {
        this(
                product.getId(),
                product.getTitle(),
                product.getPrice(),
                product.getDescription(),
                product.getCategory(),
                product.getImage()
        );
    }
}