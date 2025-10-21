package com.github.souzafcharles.api.endpoint.cartproduct.controller;

import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.CartProductRequestDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.CartProductResponseDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.ProductSalesDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.service.CartProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartProductControllerTest {

    @Mock
    private CartProductService cartProductService;

    @InjectMocks
    private CartProductController cartProductController;

    private CartProductResponseDTO cartProductResponseDTO;
    private ProductSalesDTO productSalesDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        cartProductResponseDTO = new CartProductResponseDTO("p1", "Laptop", 1500.0, 2);
        productSalesDTO = new ProductSalesDTO("p1", "Laptop", 5);
    }

    @Test
    void addProductToCartShouldReturnCreated() {
        CartProductRequestDTO requestDTO = new CartProductRequestDTO("p1", 2);
        when(cartProductService.addProductToCart("c1", requestDTO)).thenReturn(cartProductResponseDTO);

        ResponseEntity<CartProductResponseDTO> response = cartProductController.addProductToCart("c1", requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("p1", response.getBody().productId());
        verify(cartProductService, times(1)).addProductToCart("c1", requestDTO);
    }

    @Test
    void deleteProductFromCartShouldReturnNoContent() {
        doNothing().when(cartProductService).deleteProductFromCart("c1", "p1");

        ResponseEntity<Void> response = cartProductController.deleteProductFromCart("c1", "p1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartProductService, times(1)).deleteProductFromCart("c1", "p1");
    }

    @Test
    void getProductsInCartShouldReturnList() {
        when(cartProductService.getProductsInCart("c1")).thenReturn(List.of(cartProductResponseDTO));

        ResponseEntity<List<CartProductResponseDTO>> response = cartProductController.getProductsInCart("c1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("p1", response.getBody().get(0).productId());
    }

    @Test
    void getMostSoldProductsShouldReturnList() {
        when(cartProductService.getMostSoldProducts(5)).thenReturn(List.of(productSalesDTO));

        ResponseEntity<List<ProductSalesDTO>> response = cartProductController.getMostSoldProducts(5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(5, response.getBody().get(0).totalSold());
    }

    @Test
    void getMostSoldProductsByCategoryShouldReturnList() {
        when(cartProductService.getMostSoldProductsByCategory("electronics", 3))
                .thenReturn(List.of(productSalesDTO));

        ResponseEntity<List<ProductSalesDTO>> response =
                cartProductController.getMostSoldProductsByCategory("electronics", 3);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("p1", response.getBody().get(0).productId());
    }

    @Test
    void getRevenuePerProductShouldReturnMap() {
        when(cartProductService.getRevenuePerProduct()).thenReturn(Map.of("Laptop", 3000.0));

        ResponseEntity<Map<String, Double>> response = cartProductController.getRevenuePerProduct();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3000.0, response.getBody().get("Laptop"));
    }

    @Test
    void getTotalItemsInCartsShouldReturnValue() {
        when(cartProductService.getTotalItemsInCarts()).thenReturn(7L);

        ResponseEntity<Long> response = cartProductController.getTotalItemsInCarts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(7L, response.getBody());
    }

    @Test
    void getCartsContainingProductShouldReturnList() {
        when(cartProductService.getCartsContainingProduct("p1")).thenReturn(List.of("c1"));

        ResponseEntity<List<String>> response = cartProductController.getCartsContainingProduct("p1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("c1", response.getBody().get(0));
    }
}
