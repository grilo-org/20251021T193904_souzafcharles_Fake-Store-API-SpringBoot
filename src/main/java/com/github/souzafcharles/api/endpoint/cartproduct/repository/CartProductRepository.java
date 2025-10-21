package com.github.souzafcharles.api.endpoint.cartproduct.repository;

import com.github.souzafcharles.api.endpoint.cartproduct.model.entity.CartProduct;
import com.github.souzafcharles.api.endpoint.cartproduct.model.entity.CartProductId;
import com.github.souzafcharles.api.endpoint.cartproduct.model.projection.CartProductView;
import com.github.souzafcharles.api.endpoint.cartproduct.model.projection.ProductSalesView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartProductRepository extends JpaRepository<CartProduct, CartProductId> {

    @Query("""
        SELECT cp.product.id AS productId,
               cp.product.title AS productTitle,
               cp.product.price AS productPrice,
               cp.quantity AS quantity
        FROM CartProduct cp
        WHERE cp.cart.id = :cartId
    """)
    List<CartProductView> findByCartId(@Param("cartId") String cartId);

    @Query("""
        SELECT cp.product.id AS productId,
               cp.product.title AS title,
               SUM(cp.quantity) AS totalSold
        FROM CartProduct cp
        GROUP BY cp.product.id, cp.product.title
        ORDER BY SUM(cp.quantity) DESC
    """)
    List<ProductSalesView> findMostSoldProducts();

    @Query("""
        SELECT cp.product.id AS productId,
               cp.product.title AS title,
               SUM(cp.quantity) AS totalSold
        FROM CartProduct cp
        WHERE (:category IS NULL OR cp.product.category = :category)
        GROUP BY cp.product.id, cp.product.title
        ORDER BY SUM(cp.quantity) DESC
    """)
    List<ProductSalesView> findMostSoldProductsByCategory(@Param("category") String category);

    @Query("""
        SELECT cp.product.title, SUM(cp.quantity * cp.product.price)
        FROM CartProduct cp
        GROUP BY cp.product.title
    """)
    List<Object[]> findRevenuePerProductRaw();

    @Query("SELECT SUM(cp.quantity) FROM CartProduct cp")
    Long countTotalItems();

    @Query("""
        SELECT DISTINCT cp.cart.id
        FROM CartProduct cp
        WHERE cp.product.id = :productId
    """)
    List<String> findCartsByProductId(@Param("productId") String productId);
}
