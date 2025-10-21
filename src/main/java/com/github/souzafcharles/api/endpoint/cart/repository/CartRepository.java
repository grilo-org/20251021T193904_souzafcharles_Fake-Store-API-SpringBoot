package com.github.souzafcharles.api.endpoint.cart.repository;

import com.github.souzafcharles.api.endpoint.cart.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    List<Cart> findByUserId(String userId);
    List<Cart> findByCartProductsProductId(String productId);
}
