package com.github.souzafcharles.api.exceptions.custom;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException forUser(Object userId) {
        return new ResourceNotFoundException(String.format(
                com.github.souzafcharles.api.utils.Messages.USER_NOT_FOUND, userId));
    }

    public static ResourceNotFoundException forProduct(Object productId) {
        return new ResourceNotFoundException(String.format(
                com.github.souzafcharles.api.utils.Messages.PRODUCT_NOT_FOUND, productId));
    }

    public static ResourceNotFoundException forCart(Object cartId) {
        return new ResourceNotFoundException(String.format(
                com.github.souzafcharles.api.utils.Messages.CART_NOT_FOUND, cartId));
    }
}

