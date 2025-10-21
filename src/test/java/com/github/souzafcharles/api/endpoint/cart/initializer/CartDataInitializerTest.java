package com.github.souzafcharles.api.endpoint.cart.initializer;

import com.github.souzafcharles.api.client.FakeStoreClient;
import com.github.souzafcharles.api.endpoint.cart.model.dto.CartResponseDTO;
import com.github.souzafcharles.api.endpoint.cart.model.entity.Cart;
import com.github.souzafcharles.api.endpoint.cartproduct.model.dto.CartProductResponseDTO;
import com.github.souzafcharles.api.endpoint.cartproduct.model.entity.CartProduct;
import com.github.souzafcharles.api.endpoint.cart.repository.CartRepository;
import com.github.souzafcharles.api.endpoint.product.model.entity.Product;
import com.github.souzafcharles.api.endpoint.product.repository.ProductRepository;
import com.github.souzafcharles.api.endpoint.user.model.entity.User;
import com.github.souzafcharles.api.endpoint.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.argThat;

class CartDataInitializerTest {

    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private FakeStoreClient fakeStoreClient;
    private CartDataInitializer initializer;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        // Arrange
        cartRepository = mock(CartRepository.class);
        productRepository = mock(ProductRepository.class);
        userRepository = mock(UserRepository.class);
        fakeStoreClient = mock(FakeStoreClient.class);

        initializer = new CartDataInitializer(cartRepository, productRepository, userRepository, fakeStoreClient);

        user = new User();
        user.setId("u1");
        user.setUsername("John");

        product = new Product();
        product.setId("p1");
        product.setTitle("Laptop");
    }

    @Test
    void initShouldNotSaveWhenRepositoryNotEmpty() {
        // Arrange
        when(cartRepository.count()).thenReturn(1L);

        // Act
        initializer.init();

        // Assert
        verify(fakeStoreClient, never()).getAllCarts();
        verify(cartRepository, never()).saveAll(anyList());
    }

    @Test
    void initShouldNotSaveWhenFakeStoreReturnsNull() {
        // Arrange
        when(cartRepository.count()).thenReturn(0L);
        when(fakeStoreClient.getAllCarts()).thenReturn(Mono.justOrEmpty(null));

        // Act
        initializer.init();

        // Assert
        verify(cartRepository, never()).saveAll(anyList());
    }

    @Test
    void initShouldNotSaveWhenFakeStoreReturnsEmptyArray() {
        // Arrange
        when(cartRepository.count()).thenReturn(0L);
        when(fakeStoreClient.getAllCarts()).thenReturn(Mono.just(new CartResponseDTO[0]));

        // Act
        initializer.init();

        // Assert
        verify(cartRepository, never()).saveAll(anyList());
    }

    @Test
    void initShouldIgnoreCartIfUserNotFound() {
        // Arrange
        when(cartRepository.count()).thenReturn(0L);
        CartResponseDTO cartDTO = new CartResponseDTO("c1", "u1", List.of(new CartProductResponseDTO("p1", "Laptop", 1500.0, 1)));
        when(fakeStoreClient.getAllCarts()).thenReturn(Mono.just(new CartResponseDTO[]{cartDTO}));
        when(userRepository.findById("u1")).thenReturn(Optional.empty());

        // Act
        initializer.init();

        // Assert
        verify(cartRepository, never()).saveAll(anyList());
    }

    @Test
    void initShouldIgnoreCartProductIfProductNotFound() {
        // Arrange
        when(cartRepository.count()).thenReturn(0L);
        CartResponseDTO cartDTO = new CartResponseDTO("c1", "u1", List.of(new CartProductResponseDTO("p1", "Laptop", 1500.0, 1)));
        when(fakeStoreClient.getAllCarts()).thenReturn(Mono.just(new CartResponseDTO[]{cartDTO}));
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(productRepository.findById("p1")).thenReturn(Optional.empty());

        // Act
        initializer.init();

        // Assert
        verify(cartRepository, times(1)).saveAll(argThat(cartsIterable -> {
            List<Cart> carts = new ArrayList<>();
            cartsIterable.forEach(carts::add);

            if (carts.size() != 1) return false;
            Cart savedCart = carts.get(0);
            // Cart sem produtos válidos
            return savedCart.getCartProducts().isEmpty() && savedCart.getUser().equals(user) && savedCart.getId().equals("c1");
        }));
    }

    @Test
    void initShouldSaveValidCart() {
        // Arrange
        when(cartRepository.count()).thenReturn(0L);
        CartResponseDTO cartDTO = new CartResponseDTO("c1", "u1", List.of(new CartProductResponseDTO("p1", "Laptop", 1500.0, 2)));
        when(fakeStoreClient.getAllCarts()).thenReturn(Mono.just(new CartResponseDTO[]{cartDTO}));
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        // Act
        initializer.init();

        // Assert
        verify(cartRepository, times(1)).saveAll(argThat(cartsIterable -> {
            List<Cart> carts = new ArrayList<>();
            cartsIterable.forEach(carts::add);

            if (carts.size() != 1) return false;
            Cart savedCart = carts.get(0);
            if (!savedCart.getId().equals("c1")) return false;
            if (!savedCart.getUser().equals(user)) return false;

            List<CartProduct> cartProducts = savedCart.getCartProducts();
            if (cartProducts.size() != 1) return false;

            CartProduct cp = cartProducts.get(0);
            return cp.getProduct().equals(product) && cp.getQuantity() == 2;
        }));
    }
}