package com.github.souzafcharles.api.endpoint.product.initializer;

import com.github.souzafcharles.api.client.FakeStoreClient;
import com.github.souzafcharles.api.endpoint.product.model.dto.ProductResponseDTO;
import com.github.souzafcharles.api.endpoint.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class ProductDataInitializerTest {

    private ProductRepository productRepository;
    private FakeStoreClient fakeStoreClient;
    private ProductDataInitializer initializer;

    @BeforeEach
    void setup() {
        // Arrange
        productRepository = mock(ProductRepository.class);
        fakeStoreClient = mock(FakeStoreClient.class);
        initializer = new ProductDataInitializer(productRepository, fakeStoreClient);
    }

    @Test
    void initShouldSaveProductsWhenRepositoryIsEmpty() {
        // Arrange
        when(productRepository.count()).thenReturn(0L);
        ProductResponseDTO dto = new ProductResponseDTO("1", "Laptop", 1500.0, "Gaming", "Electronics", null);
        when(fakeStoreClient.getAllProducts()).thenReturn(Mono.just(new ProductResponseDTO[]{dto}));

        // Act
        initializer.init();

        // Assert
        verify(productRepository, times(1)).saveAll(argThat(iterable ->
                StreamSupport.stream(iterable.spliterator(), false)
                        .anyMatch(p -> p.getId().equals("1") && p.getTitle().equals("Laptop"))
        ));
        verify(fakeStoreClient, times(1)).getAllProducts();
    }

    @Test
    void initShouldNotSaveWhenRepositoryNotEmpty() {
        // Arrange
        when(productRepository.count()).thenReturn(5L);

        // Act
        initializer.init();

        // Assert
        verify(fakeStoreClient, never()).getAllProducts();
        verify(productRepository, never()).saveAll(anyList());
    }

    @Test
    void initShouldNotSaveWhenFakeStoreReturnsEmptyArray() {
        // Arrange
        when(productRepository.count()).thenReturn(0L);
        when(fakeStoreClient.getAllProducts()).thenReturn(Mono.just(new ProductResponseDTO[0]));

        // Act
        initializer.init();

        // Assert
        verify(fakeStoreClient, times(1)).getAllProducts();
        verify(productRepository, never()).saveAll(anyList());
    }

    @Test
    void initShouldNotSaveWhenFakeStoreReturnsNull() {
        // Arrange
        when(productRepository.count()).thenReturn(0L);
        when(fakeStoreClient.getAllProducts()).thenReturn(Mono.justOrEmpty(null));

        // Act
        initializer.init();

        // Assert
        verify(fakeStoreClient, times(1)).getAllProducts();
        verify(productRepository, never()).saveAll(anyList());
    }
}