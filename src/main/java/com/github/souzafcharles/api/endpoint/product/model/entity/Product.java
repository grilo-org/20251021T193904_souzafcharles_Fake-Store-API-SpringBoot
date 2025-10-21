package com.github.souzafcharles.api.endpoint.product.model.entity;

import com.github.souzafcharles.api.endpoint.cartproduct.model.entity.CartProduct;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_product")
public class Product {

    @Id
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double price;

    @Column(length = 5000)
    private String description;

    @Column(nullable = false)
    private String category;

    private String image;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartProduct> cartProducts = new ArrayList<>();

    public Product() {}

    @PrePersist
    public void generateIdIfNull() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    // Getters e setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<CartProduct> getCartProducts() {
        return cartProducts;
    }

    public void setCartProducts(List<CartProduct> cartProducts) {
        this.cartProducts = cartProducts;
    }
}