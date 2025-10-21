
package com.github.souzafcharles.api.endpoint.cartproduct.model.projection;

public interface CartProductView {
    String getProductId();
    String getProductTitle();
    Double getProductPrice();
    Integer getQuantity();
}
