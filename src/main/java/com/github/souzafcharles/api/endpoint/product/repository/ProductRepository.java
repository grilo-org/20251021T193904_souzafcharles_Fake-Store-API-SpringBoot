package com.github.souzafcharles.api.endpoint.product.repository;

import com.github.souzafcharles.api.endpoint.product.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByTitleContainingIgnoreCase(String title);
    List<Product> findAllByOrderByPriceDesc();
    List<Product> findAllByOrderByPriceAsc();
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product> findByDescriptionContainingIgnoreCase(String keyword);
}
