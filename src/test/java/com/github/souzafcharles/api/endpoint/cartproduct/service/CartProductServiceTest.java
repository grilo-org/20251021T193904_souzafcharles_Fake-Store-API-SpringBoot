package com.github.souzafcharles.api.endpoint.cartproduct.service;

import com.github.souzafcharles.api.endpoint.cart.model.entity.Cart;
import com.github.souzafcharles.api.endpoint.cart.repository.CartRepository;
import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.CartProductRequestDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.CartProductResponseDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.ProductSalesDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.model.entity.CartProduct;
import com.github.souzafcharles.api.endpoint.cartproduct.model.projection.CartProductView;
import com.github.souzafcharles.api.endpoint.cartproduct.model.projection.ProductSalesView;
import com.github.souzafcharles.api.endpoint.product.model.entity.Product;
import com.github.souzafcharles.api.endpoint.product.repository.ProductRepository;
import com.github.souzafcharles.api.exceptions.custom.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartProductServiceTest {

    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private com.github.souzafcharles.api.endpoint.cartproduct.repository.CartProductRepository cartProductRepository;
    private CartProductService cartProductService;

    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);
        productRepository = mock(ProductRepository.class);
        cartProductRepository = mock(com.github.souzafcharles.api.endpoint.cartproduct.repository.CartProductRepository.class);

        cartProductService = new CartProductService(cartRepository, productRepository, cartProductRepository);

        cart = new Cart();
        cart.setId("c1");
        cart.setCartProducts(new ArrayList<>());

        product = new Product();
        product.setId("p1");
        product.setTitle("Laptop");
        product.setPrice(1500.0);
    }

    @Test
    void addProductToCartShouldAddNewProduct() {
        CartProductRequestDTO requestDTO = new CartProductRequestDTO("p1", 2);
        when(cartRepository.findById("c1")).thenReturn(java.util.Optional.of(cart));
        when(productRepository.findById("p1")).thenReturn(java.util.Optional.of(product));

        CartProductResponseDTO response = cartProductService.addProductToCart("c1", requestDTO);

        assertEquals("p1", response.productId());
        assertEquals(2, response.quantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void addProductToCartShouldIncreaseQuantityIfExists() {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCart(cart);
        cartProduct.setQuantity(1);
        cart.getCartProducts().add(cartProduct);

        CartProductRequestDTO requestDTO = new CartProductRequestDTO("p1", 3);
        when(cartRepository.findById("c1")).thenReturn(java.util.Optional.of(cart));
        when(productRepository.findById("p1")).thenReturn(java.util.Optional.of(product));

        CartProductResponseDTO response = cartProductService.addProductToCart("c1", requestDTO);

        assertEquals(4, response.quantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void addProductToCartShouldThrowWhenCartNotFound() {
        when(cartRepository.findById("c1")).thenReturn(java.util.Optional.empty());
        CartProductRequestDTO requestDTO = new CartProductRequestDTO("p1", 1);

        assertThrows(ResourceNotFoundException.class, () -> cartProductService.addProductToCart("c1", requestDTO));
    }

    @Test
    void addProductToCartShouldThrowWhenProductNotFound() {
        when(cartRepository.findById("c1")).thenReturn(java.util.Optional.of(cart));
        when(productRepository.findById("p1")).thenReturn(java.util.Optional.empty());
        CartProductRequestDTO requestDTO = new CartProductRequestDTO("p1", 1);

        assertThrows(ResourceNotFoundException.class, () -> cartProductService.addProductToCart("c1", requestDTO));
    }

    @Test
    void deleteProductFromCartShouldRemoveProduct() {
        CartProduct cartProduct = new CartProduct();
        cartProduct.setProduct(product);
        cartProduct.setCart(cart);
        cartProduct.setQuantity(1);
        cart.getCartProducts().add(cartProduct);

        when(cartRepository.findById("c1")).thenReturn(java.util.Optional.of(cart));

        cartProductService.deleteProductFromCart("c1", "p1");

        assertTrue(cart.getCartProducts().isEmpty());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void deleteProductFromCartShouldThrowIfProductNotFound() {
        when(cartRepository.findById("c1")).thenReturn(java.util.Optional.of(cart));

        assertThrows(ResourceNotFoundException.class, () -> cartProductService.deleteProductFromCart("c1", "p1"));
    }

    @Test
    void getProductsInCartShouldReturnList() {
        CartProductView view = mock(CartProductView.class);
        when(view.getProductId()).thenReturn("p1");
        when(view.getProductTitle()).thenReturn("Laptop");
        when(view.getProductPrice()).thenReturn(1500.0);
        when(view.getQuantity()).thenReturn(2);

        when(cartProductRepository.findByCartId("c1")).thenReturn(List.of(view));

        List<CartProductResponseDTO> result = cartProductService.getProductsInCart("c1");

        assertEquals(1, result.size());
        assertEquals("p1", result.get(0).productId());
        assertEquals(2, result.get(0).quantity());
    }

    @Test
    void getMostSoldProductsShouldReturnTopProducts() {
        ProductSalesView view = mock(ProductSalesView.class);
        when(view.getProductId()).thenReturn("p1");
        when(view.getTitle()).thenReturn("Laptop");
        when(view.getTotalSold()).thenReturn(5);

        when(cartProductRepository.findMostSoldProducts()).thenReturn(List.of(view));

        List<ProductSalesDTO> result = cartProductService.getMostSoldProducts(1);

        assertEquals(1, result.size());
        assertEquals("p1", result.get(0).productId());
        assertEquals(5, result.get(0).totalSold());
    }

    @Test
    void getMostSoldProductsByCategoryShouldReturnTopProducts() {
        ProductSalesView view = mock(ProductSalesView.class);
        when(view.getProductId()).thenReturn("p1");
        when(view.getTitle()).thenReturn("Laptop");
        when(view.getTotalSold()).thenReturn(5);

        when(cartProductRepository.findMostSoldProductsByCategory("electronics")).thenReturn(List.of(view));

        List<ProductSalesDTO> result = cartProductService.getMostSoldProductsByCategory("electronics", 1);

        assertEquals(1, result.size());
        assertEquals("p1", result.get(0).productId());
        assertEquals(5, result.get(0).totalSold());
    }

    @Test
    void getRevenuePerProductShouldReturnMap() {
        Object[] row = new Object[]{"Laptop", 3000.0};
        when(cartProductRepository.findRevenuePerProductRaw()).thenReturn(List.<Object[]>of(row));

        Map<String, Double> revenue = cartProductService.getRevenuePerProduct();

        assertEquals(1, revenue.size());
        assertEquals(3000.0, revenue.get("Laptop"));
    }

    @Test
    void getTotalItemsInCartsShouldReturnSum() {
        when(cartProductRepository.countTotalItems()).thenReturn(3L);

        long total = cartProductService.getTotalItemsInCarts();

        assertEquals(3, total);
    }

    @Test
    void getCartsContainingProductShouldReturnCartIds() {
        when(cartProductRepository.findCartsByProductId("p1")).thenReturn(List.of("c1"));

        List<String> result = cartProductService.getCartsContainingProduct("p1");

        assertEquals(1, result.size());
        assertEquals("c1", result.get(0));
    }
}
