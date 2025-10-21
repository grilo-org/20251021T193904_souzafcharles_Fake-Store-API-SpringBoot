package com.github.souzafcharles.api.endpoint.cartproduct.service;

import com.github.souzafcharles.api.endpoint.cart.model.entity.Cart;
import com.github.souzafcharles.api.endpoint.cart.repository.CartRepository;
import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.CartProductRequestDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.CartProductResponseDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.ProductSalesDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.model.entity.CartProduct;
import com.github.souzafcharles.api.endpoint.cartproduct.model.entity.CartProductId;
import com.github.souzafcharles.api.endpoint.cartproduct.model.projection.CartProductView;
import com.github.souzafcharles.api.endpoint.product.model.entity.Product;
import com.github.souzafcharles.api.endpoint.product.repository.ProductRepository;
import com.github.souzafcharles.api.exceptions.custom.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartProductService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final com.github.souzafcharles.api.endpoint.cartproduct.repository.CartProductRepository cartProductRepository;

    public CartProductService(CartRepository cartRepository,
                              ProductRepository productRepository,
                              com.github.souzafcharles.api.endpoint.cartproduct.repository.CartProductRepository cartProductRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartProductRepository = cartProductRepository;
    }

    public CartProductResponseDTO addProductToCart(String cartId, CartProductRequestDTO dto) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> ResourceNotFoundException.forCart(cartId));

        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> ResourceNotFoundException.forProduct(dto.productId()));

        CartProduct cartProduct = cart.getCartProducts().stream()
                .filter(cp -> cp.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElseGet(() -> {
                    CartProduct cp = new CartProduct();
                    cp.setId(new CartProductId());
                    cp.setCart(cart);
                    cp.setProduct(product);
                    cart.getCartProducts().add(cp);
                    return cp;
                });

        cartProduct.setQuantity((cartProduct.getQuantity() == null ? 0 : cartProduct.getQuantity()) + dto.quantity());
        cartRepository.save(cart);

        return new CartProductResponseDTO(cartProduct);
    }

    public void deleteProductFromCart(String cartId, String productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> ResourceNotFoundException.forCart(cartId));

        boolean removed = cart.getCartProducts().removeIf(cp -> cp.getProduct().getId().equals(productId));
        if (!removed) throw ResourceNotFoundException.forProduct(productId);

        cartRepository.save(cart);
    }

    public List<CartProductResponseDTO> getProductsInCart(String cartId) {
        List<CartProductView> views = cartProductRepository.findByCartId(cartId);
        return views.stream()
                .map(v -> new CartProductResponseDTO(v.getProductId(), v.getProductTitle(), v.getProductPrice(), v.getQuantity()))
                .collect(Collectors.toList());
    }

    public List<ProductSalesDTO> getMostSoldProducts(int topN) {
        return cartProductRepository.findMostSoldProducts().stream()
                .limit(topN)
                .map(v -> new ProductSalesDTO(v.getProductId(), v.getTitle(), v.getTotalSold()))
                .collect(Collectors.toList());
    }

    public List<ProductSalesDTO> getMostSoldProductsByCategory(String category, int topN) {
        return cartProductRepository.findMostSoldProductsByCategory(category).stream()
                .limit(topN)
                .map(v -> new ProductSalesDTO(v.getProductId(), v.getTitle(), v.getTotalSold()))
                .collect(Collectors.toList());
    }

    public Map<String, Double> getRevenuePerProduct() {
        return cartProductRepository.findRevenuePerProductRaw().stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> ((Number) row[1]).doubleValue()
                ));
    }

    public long getTotalItemsInCarts() {
        Long count = cartProductRepository.countTotalItems();
        return count == null ? 0 : count;
    }

    public List<String> getCartsContainingProduct(String productId) {
        return cartProductRepository.findCartsByProductId(productId);
    }
}
