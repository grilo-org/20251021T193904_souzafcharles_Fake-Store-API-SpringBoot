package com.github.souzafcharles.api.client;

import com.github.souzafcharles.api.endpoint.cart.model.dto.CartResponseDTO;
import com.github.souzafcharles.api.endpoint.product.model.dto.ProductResponseDTO;
import com.github.souzafcharles.api.endpoint.user.model.dto.UserResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class FakeStoreClient {

    private final WebClient webClient;

    public FakeStoreClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ProductResponseDTO[]> getAllProducts() {
        return webClient.get()
                .uri("https://fakestoreapi.com/products")
                .retrieve()
                .bodyToMono(ProductResponseDTO[].class);
    }

    public Mono<CartResponseDTO[]> getAllCarts() {
        return webClient.get()
                .uri("https://fakestoreapi.com/carts")
                .retrieve()
                .bodyToMono(CartResponseDTO[].class);
    }

    public Mono<UserResponseDTO[]> getAllUsers() {
        return webClient.get()
                .uri("https://fakestoreapi.com/users")
                .retrieve()
                .bodyToMono(UserResponseDTO[].class);
    }
}
