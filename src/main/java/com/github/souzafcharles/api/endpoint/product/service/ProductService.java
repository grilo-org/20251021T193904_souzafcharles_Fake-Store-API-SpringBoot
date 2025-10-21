package com.github.souzafcharles.api.endpoint.product.service;

import com.github.souzafcharles.api.client.FakeStoreClient;
import com.github.souzafcharles.api.endpoint.product.model.dto.ProductRequestDTO;
import com.github.souzafcharles.api.endpoint.product.model.dto.ProductResponseDTO;
import com.github.souzafcharles.api.endpoint.product.model.entity.Product;
import com.github.souzafcharles.api.endpoint.product.repository.ProductRepository;
import com.github.souzafcharles.api.exceptions.custom.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
        List<ProductResponseDTO> allProducts = productRepository.findAll()
                .stream()
                .map(ProductResponseDTO::new)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allProducts.size());
        return new PageImpl<>(allProducts.subList(start, end), pageable, allProducts.size());
    }

    public ProductResponseDTO getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forProduct(id));
        return new ProductResponseDTO(product);
    }

    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        Product product = new Product();
        product.setTitle(dto.title());
        product.setPrice(dto.price());
        product.setDescription(dto.description());
        product.setCategory(dto.category());
        product.setImage(dto.image());
        return new ProductResponseDTO(productRepository.save(product));
    }

    public ProductResponseDTO updateProduct(String id, ProductRequestDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forProduct(id));
        product.setTitle(dto.title());
        product.setPrice(dto.price());
        product.setDescription(dto.description());
        product.setCategory(dto.category());
        product.setImage(dto.image());
        return new ProductResponseDTO(productRepository.save(product));
    }

    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forProduct(id));
        productRepository.delete(product);
    }

    public List<ProductResponseDTO> searchProducts(String keyword) {
        Set<Product> results = new HashSet<>();
        results.addAll(productRepository.findByTitleContainingIgnoreCase(keyword));
        results.addAll(productRepository.findByDescriptionContainingIgnoreCase(keyword));
        return results.stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getTopExpensiveProducts(int topN) {
        return productRepository.findAllByOrderByPriceDesc().stream()
                .limit(topN)
                .map(ProductResponseDTO::new)
                .toList();
    }

    public List<ProductResponseDTO> getTopCheapestProducts(int topN) {
        return productRepository.findAllByOrderByPriceAsc().stream()
                .limit(topN)
                .map(ProductResponseDTO::new)
                .toList();
    }

    public Map<String, Double> getAveragePricePerCategory() {
        return productRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.averagingDouble(Product::getPrice)
                ));
    }

    public List<ProductResponseDTO> getProductsByPriceRange(Double min, Double max) {
        return productRepository.findByPriceBetween(min, max).stream()
                .map(ProductResponseDTO::new)
                .toList();
    }
}