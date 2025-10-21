package com.github.souzafcharles.api.endpoint.cart.service;

import com.github.souzafcharles.api.endpoint.cart.model.dto.CartRequestDTO;
import com.github.souzafcharles.api.endpoint.cart.model.dto.CartResponseDTO;
import com.github.souzafcharles.api.endpoint.cart.model.entity.Cart;
import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.CartProductRequestDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.model.entity.CartProduct;
import com.github.souzafcharles.api.endpoint.product.model.entity.Product;
import com.github.souzafcharles.api.endpoint.product.repository.ProductRepository;
import com.github.souzafcharles.api.endpoint.user.model.entity.User;
import com.github.souzafcharles.api.endpoint.user.repository.UserRepository;
import com.github.souzafcharles.api.endpoint.cart.repository.CartRepository;
import com.github.souzafcharles.api.exceptions.custom.DatabaseException;
import com.github.souzafcharles.api.exceptions.custom.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    private CartRepository cartRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private CartService cartService;

    private User user;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        // Arrange common mocks
        cartRepository = mock(CartRepository.class);
        userRepository = mock(UserRepository.class);
        productRepository = mock(ProductRepository.class);

        cartService = new CartService(cartRepository, userRepository, productRepository);

        user = new User();
        user.setId("u1");
        user.setUsername("John");

        product = new Product();
        product.setId("p1");
        product.setTitle("Laptop");
        product.setPrice(1500.0);

        cart = new Cart();
        cart.setId("c1");
        cart.setUser(user);
        CartProduct cp = new CartProduct();
        cp.setCart(cart);
        cp.setProduct(product);
        cp.setQuantity(2);
        cart.setCartProducts(List.of(cp));
    }


    @Test
    void getAllCartsShouldReturnPagedResult() {
        // Arrange
        when(cartRepository.findAll()).thenReturn(List.of(cart));
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<CartResponseDTO> page = cartService.getAllCarts(pageable);

        // Assert
        assertEquals(1, page.getTotalElements());
        assertEquals("c1", page.getContent().get(0).id());
    }


    @Test
    void getCartByIdShouldReturnCart() {
        // Arrange
        when(cartRepository.findById("c1")).thenReturn(Optional.of(cart));

        // Act
        CartResponseDTO response = cartService.getCartById("c1");

        // Assert
        assertEquals("c1", response.id());
        assertEquals("u1", response.userId());
    }

    @Test
    void getCartByIdShouldThrowWhenNotFound() {
        // Arrange
        when(cartRepository.findById("c1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.getCartById("c1"));
    }

    @Test
    void createCartShouldReturnSavedCart() {
        // Arrange
        CartProductRequestDTO productDTO = new CartProductRequestDTO("p1", 2);
        CartRequestDTO requestDTO = new CartRequestDTO("u1", List.of(productDTO));
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(cartRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CartResponseDTO response = cartService.createCart(requestDTO);

        // Assert
        assertEquals("u1", response.userId());
        assertEquals(1, response.products().size());
        assertEquals(2, response.products().get(0).quantity());
    }

    @Test
    void createCartShouldThrowWhenUserNotFound() {
        // Arrange
        CartRequestDTO requestDTO = new CartRequestDTO("u1", List.of(new CartProductRequestDTO("p1", 1)));
        when(userRepository.findById("u1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.createCart(requestDTO));
    }

    @Test
    void createCartShouldThrowWhenProductNotFound() {
        // Arrange
        CartRequestDTO requestDTO = new CartRequestDTO("u1", List.of(new CartProductRequestDTO("p1", 1)));
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(productRepository.findById("p1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> cartService.createCart(requestDTO));
    }

    @Test
    void updateCartShouldReturnUpdatedCart() {
        // Arrange
        CartRequestDTO requestDTO = new CartRequestDTO("u1", List.of(new CartProductRequestDTO("p1", 3)));
        when(cartRepository.findById("c1")).thenReturn(Optional.of(cart));
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(cartRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CartResponseDTO response = cartService.updateCart("c1", requestDTO);

        // Assert
        assertEquals(1, response.products().size());
        assertEquals(3, response.products().get(0).quantity());
    }

    @Test
    void deleteCartShouldDelete() {
        // Arrange
        when(cartRepository.findById("c1")).thenReturn(Optional.of(cart));

        // Act
        cartService.deleteCart("c1");

        // Assert
        verify(cartRepository, times(1)).delete(cart);
    }

    @Test
    void deleteCartShouldThrowDatabaseExceptionOnIntegrityViolation() {
        // Arrange
        when(cartRepository.findById("c1")).thenReturn(Optional.of(cart));
        doThrow(new DataIntegrityViolationException("")).when(cartRepository).delete(cart);

        // Act & Assert
        assertThrows(DatabaseException.class, () -> cartService.deleteCart("c1"));
    }

    @Test
    void getCartsByUserIdShouldReturnList() {
        // Arrange
        when(cartRepository.findByUserId("u1")).thenReturn(List.of(cart));

        // Act
        List<CartResponseDTO> result = cartService.getCartsByUserId("u1");

        // Assert
        assertEquals(1, result.size());
        assertEquals("c1", result.get(0).id());
    }

    @Test
    void getCartsByProductIdShouldReturnList() {
        // Arrange
        when(cartRepository.findByCartProductsProductId("p1")).thenReturn(List.of(cart));

        // Act
        List<CartResponseDTO> result = cartService.getCartsByProductId("p1");

        // Assert
        assertEquals(1, result.size());
        assertEquals("c1", result.get(0).id());
    }

    @Test
    void getTotalProductsForUserShouldReturnSum() {
        // Act
        when(cartRepository.findByUserId("u1")).thenReturn(List.of(cart));

        // Act
        long total = cartService.getTotalProductsForUser("u1");

        // Assert
        assertEquals(2, total);
    }

    @Test
    void getCartsWithTotalValueGreaterThanShouldReturnFilteredList() {
        // Arrange
        when(cartRepository.findAll()).thenReturn(List.of(cart));

        // Act
        List<CartResponseDTO> result = cartService.getCartsWithTotalValueGreaterThan(1000.0);

        // Assert
        assertEquals(1, result.size());

        // Act with higher minTotal
        result = cartService.getCartsWithTotalValueGreaterThan(5000.0);
        assertTrue(result.isEmpty());
    }
}
