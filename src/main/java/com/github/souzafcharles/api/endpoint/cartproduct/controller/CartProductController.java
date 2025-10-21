package com.github.souzafcharles.api.endpoint.cartproduct.controller;

import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.CartProductRequestDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.CartProductResponseDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.ProductSalesDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.service.CartProductService;
import com.github.souzafcharles.api.utils.Messages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart-products")
@Tag(name = "Cart Product API", description = Messages.CART_PRODUCT_TAG_DESCRIPTION)
public class CartProductController {

    private final CartProductService cartProductService;

    public CartProductController(CartProductService cartProductService) {
        this.cartProductService = cartProductService;
    }

    @PostMapping("/{cartId}")
    @Operation(summary = Messages.CART_PRODUCT_ADD_SUMMARY,
            description = Messages.CART_PRODUCT_ADD_DESCRIPTION)
    public ResponseEntity<CartProductResponseDTO> addProductToCart(
            @PathVariable String cartId,
            @RequestBody @Valid CartProductRequestDTO dto
    ) {
        CartProductResponseDTO response = cartProductService.addProductToCart(cartId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{cartId}/{productId}")
    @Operation(summary = Messages.CART_PRODUCT_REMOVE_SUMMARY,
            description = Messages.CART_PRODUCT_REMOVE_DESCRIPTION)
    public ResponseEntity<Void> deleteProductFromCart(
            @PathVariable String cartId,
            @PathVariable String productId
    ) {
        cartProductService.deleteProductFromCart(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{cartId}")
    @Operation(summary = Messages.CART_PRODUCT_LIST_SUMMARY,
            description = Messages.CART_PRODUCT_LIST_DESCRIPTION)
    public ResponseEntity<List<CartProductResponseDTO>> getProductsInCart(@PathVariable String cartId) {
        return ResponseEntity.ok(cartProductService.getProductsInCart(cartId));
    }

    // --- Analytics ---

    @GetMapping("/analytics/most-sold")
    @Operation(summary = Messages.CART_PRODUCT_MOST_SOLD_SUMMARY,
            description = Messages.CART_PRODUCT_MOST_SOLD_DESCRIPTION)
    public ResponseEntity<List<ProductSalesDTO>> getMostSoldProducts(
            @RequestParam(defaultValue = "5") int topN
    ) {
        return ResponseEntity.ok(cartProductService.getMostSoldProducts(topN));
    }

    @GetMapping("/analytics/most-sold-by-category")
    @Operation(summary = "Most Sold Products by Category",
            description = "Get the top N most sold products filtered by category")
    public ResponseEntity<List<ProductSalesDTO>> getMostSoldProductsByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "5") int topN
    ) {
        return ResponseEntity.ok(cartProductService.getMostSoldProductsByCategory(category, topN));
    }

    @GetMapping("/analytics/revenue")
    @Operation(summary = Messages.CART_PRODUCT_REVENUE_SUMMARY,
            description = Messages.CART_PRODUCT_REVENUE_DESCRIPTION)
    public ResponseEntity<Map<String, Double>> getRevenuePerProduct() {
        return ResponseEntity.ok(cartProductService.getRevenuePerProduct());
    }

    @GetMapping("/analytics/total-items")
    @Operation(summary = Messages.CART_PRODUCT_TOTAL_ITEMS_SUMMARY,
            description = Messages.CART_PRODUCT_TOTAL_ITEMS_DESCRIPTION)
    public ResponseEntity<Long> getTotalItemsInCarts() {
        return ResponseEntity.ok(cartProductService.getTotalItemsInCarts());
    }

    @GetMapping("/analytics/carts-by-product/{productId}")
    @Operation(summary = Messages.CART_PRODUCT_CARTS_BY_PRODUCT_SUMMARY,
            description = Messages.CART_PRODUCT_CARTS_BY_PRODUCT_DESCRIPTION)
    public ResponseEntity<List<String>> getCartsContainingProduct(@PathVariable String productId) {
        return ResponseEntity.ok(cartProductService.getCartsContainingProduct(productId));
    }
}
