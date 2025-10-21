package com.github.souzafcharles.api.endpoint.cartproduct.model.dto;

import com.github.souzafcharles.api.utils.Messages;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.io.Serializable;

public record CartProductRequestDTO(
        @NotNull(message = Messages.PRODUCT_TITLE_REQUIRED) String productId,
        @NotNull(message = Messages.CART_PRODUCT_QUANTITY_POSITIVE)
        @Positive Integer quantity
) implements Serializable { }