package com.github.souzafcharles.api.endpoint.cartproduct.model.dto;

import com.github.souzafcharles.api.endpoint.cartproduct.model.entity.CartProduct;
import java.io.Serializable;

public record CartProductResponseDTO(
        String productId,
        String productTitle,
        Double productPrice,
        Integer quantity
) implements Serializable {
    public CartProductResponseDTO(CartProduct cartProduct) {
        this(
                cartProduct.getProduct().getId(),
                cartProduct.getProduct().getTitle(),
                cartProduct.getProduct().getPrice(),
                cartProduct.getQuantity()
        );
    }
}
