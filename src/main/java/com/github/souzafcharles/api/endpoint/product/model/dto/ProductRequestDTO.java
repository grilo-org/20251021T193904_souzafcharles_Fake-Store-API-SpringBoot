package com.github.souzafcharles.api.endpoint.product.model.dto;

import com.github.souzafcharles.api.utils.Messages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public record ProductRequestDTO(
        @NotBlank(message = Messages.PRODUCT_TITLE_REQUIRED) String title,
        @NotNull(message = Messages.PRODUCT_PRICE_REQUIRED)
        @Positive(message = Messages.PRODUCT_PRICE_POSITIVE) Double price,
        @Size(max = 5000, message = Messages.PRODUCT_DESCRIPTION_MAX_LENGTH) String description,
        @NotBlank(message = Messages.PRODUCT_CATEGORY_REQUIRED) String category,
        String image
) implements Serializable { }