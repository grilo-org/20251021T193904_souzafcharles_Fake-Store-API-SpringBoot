package com.github.souzafcharles.api.endpoint.cart.model.dto;

import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.CartProductRequestDTO;
import com.github.souzafcharles.api.utils.Messages;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public record CartRequestDTO(
        @NotNull(message = Messages.CART_USER_REQUIRED) String userId,
        @NotNull(message = Messages.CART_PRODUCTS_REQUIRED)
        List<CartProductRequestDTO> products
) implements Serializable { }