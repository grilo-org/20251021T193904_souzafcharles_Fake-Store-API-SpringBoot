package com.github.souzafcharles.api.endpoint.product.service;

import com.github.souzafcharles.api.endpoint.product.model.dto.ProductRequestDTO;
import com.github.souzafcharles.api.endpoint.product.model.entity.Product;
import com.github.souzafcharles.api.endpoint.product.repository.ProductRepository;
import com.github.souzafcharles.api.exceptions.custom.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Product anotherProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        product = new Product();
        product.setId("1");
        product.setTitle("Laptop");
        product.setPrice(1500.0);
        product.setCategory("Electronics");
        product.setDescription("Gaming laptop");

        anotherProduct = new Product();
        anotherProduct.setId("2");
        anotherProduct.setTitle("Chair");
        anotherProduct.setPrice(200.0);
        anotherProduct.setCategory("Furniture");
        anotherProduct.setDescription("Office chair");
    }

    @Test
    void getAllProductsShouldReturnPagedProducts() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(product, anotherProduct));

        // Act
        var page = productService.getAllProducts(PageRequest.of(0, 10));

        // Assert
        assertEquals(2, page.getTotalElements());
        assertEquals("Laptop", page.getContent().get(0).title());
        assertEquals("Chair", page.getContent().get(1).title());
    }

    @Test
    void getProductByIdShouldReturnProduct() {
        // Arrange
        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        // Act
        var response = productService.getProductById("1");

        // Assert
        assertEquals("Laptop", response.title());
        assertEquals("Electronics", response.category());
    }

    @Test
    void getProductByIdShouldThrowException() {
        // Arrange
        when(productRepository.findById("99")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById("99"));
    }

    @Test
    void createProductShouldSaveProduct() {
        // Arrange
        ProductRequestDTO dto = new ProductRequestDTO("Phone", 800.0, "Smartphone", "Electronics", null);
        Product savedProduct = new Product();
        savedProduct.setId("3");
        savedProduct.setTitle(dto.title());
        savedProduct.setPrice(dto.price());
        savedProduct.setCategory(dto.category());
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        var response = productService.createProduct(dto);

        // Assert
        assertEquals("Phone", response.title());
        assertEquals("3", response.id());
    }

    @Test
    void updateProductShouldUpdateProduct() {
        // Arrange
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        ProductRequestDTO dto = new ProductRequestDTO("Laptop Pro", 1800.0, "Updated", "Electronics", null);

        // Act
        var response = productService.updateProduct("1", dto);

        // Assert
        assertEquals("Laptop Pro", response.title());
        assertEquals(1800.0, response.price());
    }

    @Test
    void updateProductShouldThrowException() {
        // Arrange
        when(productRepository.findById("99")).thenReturn(Optional.empty());
        ProductRequestDTO dto = new ProductRequestDTO("NonExistent", 0.0, "", "None", null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct("99", dto));
    }

    @Test
    void deleteProductShouldRemoveProduct() {
        // Arrange
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        // Act
        assertDoesNotThrow(() -> productService.deleteProduct("1"));

        // Assert
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void deleteProductShouldThrowException() {
        // Arrange
        when(productRepository.findById("99")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct("99"));
    }

    @Test
    void searchProductsShouldReturnMatchingProducts() {
        // Arrange
        when(productRepository.findByTitleContainingIgnoreCase("Laptop")).thenReturn(List.of(product));
        when(productRepository.findByDescriptionContainingIgnoreCase("Laptop")).thenReturn(List.of(product));

        // Act
        var results = productService.searchProducts("Laptop");

        // Assert
        assertEquals(1, results.size());
        assertEquals("Laptop", results.get(0).title());
    }

    @Test
    void getTopExpensiveProductsShouldReturnTopN() {
        // Arrange
        when(productRepository.findAllByOrderByPriceDesc()).thenReturn(List.of(product, anotherProduct));

        // Act
        var top = productService.getTopExpensiveProducts(1);

        // Assert
        assertEquals(1, top.size());
        assertEquals("Laptop", top.get(0).title());
    }

    @Test
    void getTopCheapestProductsShouldReturnTopN() {
        // Arrange
        when(productRepository.findAllByOrderByPriceAsc()).thenReturn(List.of(anotherProduct, product));

        // Act
        var top = productService.getTopCheapestProducts(1);

        // Assert
        assertEquals(1, top.size());
        assertEquals("Chair", top.get(0).title());
    }

    @Test
    void getAveragePricePerCategoryShouldReturnMap() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of(product, anotherProduct));

        // Act
        Map<String, Double> averages = productService.getAveragePricePerCategory();

        // Assert
        assertEquals(2, averages.size());
        assertEquals(1500.0, averages.get("Electronics"));
        assertEquals(200.0, averages.get("Furniture"));
    }

    @Test
    void getProductsByPriceRangeShouldReturnFiltered() {
        // Arrange
        when(productRepository.findByPriceBetween(100.0, 1000.0)).thenReturn(List.of(anotherProduct));

        // Act
        var results = productService.getProductsByPriceRange(100.0, 1000.0);

        // Assert
        assertEquals(1, results.size());
        assertEquals("Chair", results.get(0).title());
    }
}