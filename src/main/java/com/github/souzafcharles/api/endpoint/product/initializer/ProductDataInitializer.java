package com.github.souzafcharles.api.endpoint.product.initializer;

import com.github.souzafcharles.api.client.FakeStoreClient;
import com.github.souzafcharles.api.endpoint.product.model.dto.ProductResponseDTO;
import com.github.souzafcharles.api.endpoint.product.model.entity.Product;
import com.github.souzafcharles.api.endpoint.product.repository.ProductRepository;
import com.github.souzafcharles.api.utils.Messages;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component("productDataInitializer")
public class ProductDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(ProductDataInitializer.class);

    private final ProductRepository productRepository;
    private final FakeStoreClient fakeStoreClient;

    public ProductDataInitializer(ProductRepository productRepository, FakeStoreClient fakeStoreClient) {
        this.productRepository = productRepository;
        this.fakeStoreClient = fakeStoreClient;
    }

    @PostConstruct
    public void init() {
        if (productRepository.count() > 0) {
            log.info(Messages.PRODUCT_ALREADY_INITIALIZED);
            return;
        }

        ProductResponseDTO[] products = fakeStoreClient.getAllProducts().block();
        if (products == null || products.length == 0) {
            log.warn(Messages.PRODUCT_NO_RETURNED);
            return;
        }

        List<Product> entities = Arrays.stream(products)
                .map(dto -> {
                    Product product = new Product();
                    product.setId(dto.id());
                    product.setTitle(dto.title());
                    product.setPrice(dto.price());
                    product.setDescription(dto.description());
                    product.setCategory(dto.category());
                    product.setImage(dto.image());
                    return product;
                })
                .toList();

        productRepository.saveAll(entities);
        log.info(Messages.PRODUCT_SAVED_SUCCESS, entities.size());
    }
}