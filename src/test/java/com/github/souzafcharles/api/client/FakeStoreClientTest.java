package com.github.souzafcharles.api.client;

import com.github.souzafcharles.api.endpoint.cart.model.dto.CartResponseDTO;
import com.github.souzafcharles.api.endpoint.product.model.dto.ProductResponseDTO;
import com.github.souzafcharles.api.endpoint.user.model.dto.UserResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FakeStoreClientTest {

    private WebClient webClient;
    private FakeStoreClient client;

    @BeforeEach
    void setup() {
        // Arrange: WebClient Mock
        webClient = mock(WebClient.class, RETURNS_DEEP_STUBS);
        client = new FakeStoreClient(webClient);
    }

    @Test
    void getAllProductsShouldReturnProducts() {
        // Arrange
        ProductResponseDTO[] mockProducts = {
                new ProductResponseDTO("1", "Laptop", 1500.0, "Gaming laptop", "Electronics", null)
        };
        when(webClient.get()
                .uri(anyString())
                .retrieve()
                .bodyToMono(ProductResponseDTO[].class))
                .thenReturn(Mono.just(mockProducts));

        // Act
        ProductResponseDTO[] result = client.getAllProducts().block();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals("Laptop", result[0].title());
    }

    @Test
    void getAllProductsShouldPropagateError() {
        // Arrange
        when(webClient.get()
                .uri(anyString())
                .retrieve()
                .bodyToMono(ProductResponseDTO[].class))
                .thenReturn(Mono.error(new RuntimeException("API error")));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> client.getAllProducts().block());
        assertEquals("API error", exception.getMessage());
    }

    @Test
    void getAllCartsShouldReturnCarts() {
        // Arrange
        CartResponseDTO[] mockCarts = {
                new CartResponseDTO("1", "user1", List.of())
        };
        when(webClient.get()
                .uri("https://fakestoreapi.com/carts")
                .retrieve()
                .bodyToMono(CartResponseDTO[].class))
                .thenReturn(Mono.just(mockCarts));

        // Act
        CartResponseDTO[] result = client.getAllCarts().block();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals("1", result[0].id());
    }

    @Test
    void getAllUsersShouldReturnUsers() {
        // Arrange
        UserResponseDTO[] mockUsers = {
                new UserResponseDTO("u1", "balthazar", "balthazar@example.com")
        };
        when(webClient.get()
                .uri("https://fakestoreapi.com/users")
                .retrieve()
                .bodyToMono(UserResponseDTO[].class))
                .thenReturn(Mono.just(mockUsers));

        // Act
        UserResponseDTO[] result = client.getAllUsers().block();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.length);
        assertEquals("balthazar", result[0].username());
    }
}