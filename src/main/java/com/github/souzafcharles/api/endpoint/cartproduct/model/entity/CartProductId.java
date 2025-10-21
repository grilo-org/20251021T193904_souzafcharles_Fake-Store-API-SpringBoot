package com.github.souzafcharles.api.endpoint.cartproduct.model.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CartProductId implements Serializable {
    private String cartId;
    private String productId;

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartProductId that = (CartProductId) o;
        return Objects.equals(cartId, that.cartId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, productId);
    }
}
