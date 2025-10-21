package com.github.souzafcharles.api.endpoint.cart.model.entity;

import com.github.souzafcharles.api.endpoint.cartproduct.model.entity.CartProduct;
import com.github.souzafcharles.api.endpoint.user.model.entity.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_cart")
public class Cart {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartProduct> cartProducts = new ArrayList<>();

    @PrePersist
    public void generateIdIfNull() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CartProduct> getCartProducts() {
        return cartProducts;
    }

    public void setCartProducts(List<CartProduct> cartProducts) {
        this.cartProducts = cartProducts;
    }
}