package com.github.souzafcharles.api.endpoint.product.controller;

import com.github.souzafcharles.api.endpoint.product.model.dto.ProductRequestDTO;
import com.github.souzafcharles.api.endpoint.product.model.dto.ProductResponseDTO;
import com.github.souzafcharles.api.endpoint.product.service.ProductService;
import com.github.souzafcharles.api.utils.Messages;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@Tag(name = "Product API", description = Messages.PRODUCT_TAG_DESCRIPTION)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = Messages.PRODUCT_GET_ALL_SUMMARY, description = Messages.PRODUCT_GET_ALL_DESCRIPTION)
    public ResponseEntity<PagedModel<EntityModel<ProductResponseDTO>>> getAll(
            Pageable pageable, PagedResourcesAssembler<ProductResponseDTO> assembler) {
        var page = productService.getAllProducts(pageable);
        var model = assembler.toModel(page, product ->
                EntityModel.of(product,
                        WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProductController.class)
                                .getById(product.id())).withSelfRel())
        );
        return page.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(model);
    }

    @GetMapping("/{id}")
    @Operation(summary = Messages.PRODUCT_GET_BY_ID_SUMMARY, description = Messages.PRODUCT_GET_BY_ID_DESCRIPTION)
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @Operation(summary = Messages.PRODUCT_CREATE_SUMMARY, description = Messages.PRODUCT_CREATE_DESCRIPTION)
    public ResponseEntity<ProductResponseDTO> create(@RequestBody @Valid ProductRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = Messages.PRODUCT_UPDATE_SUMMARY, description = Messages.PRODUCT_UPDATE_DESCRIPTION)
    public ResponseEntity<ProductResponseDTO> update(@PathVariable String id, @RequestBody @Valid ProductRequestDTO dto) {
        return ResponseEntity.ok(productService.updateProduct(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = Messages.PRODUCT_DELETE_SUMMARY, description = Messages.PRODUCT_DELETE_DESCRIPTION)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = Messages.PRODUCT_SEARCH_SUMMARY, description = Messages.PRODUCT_SEARCH_DESCRIPTION)
    public ResponseEntity<List<ProductResponseDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }

    @GetMapping("/top-expensive")
    @Operation(summary = Messages.PRODUCT_TOP_EXPENSIVE_SUMMARY, description = Messages.PRODUCT_TOP_EXPENSIVE_DESCRIPTION)
    public ResponseEntity<List<ProductResponseDTO>> topExpensive(@RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(productService.getTopExpensiveProducts(topN));
    }

    @GetMapping("/top-cheapest")
    @Operation(summary = Messages.PRODUCT_TOP_CHEAPEST_SUMMARY, description = Messages.PRODUCT_TOP_CHEAPEST_DESCRIPTION)
    public ResponseEntity<List<ProductResponseDTO>> topCheapest(@RequestParam(defaultValue = "5") int topN) {
        return ResponseEntity.ok(productService.getTopCheapestProducts(topN));
    }

    @GetMapping("/average-price-category")
    @Operation(summary = Messages.PRODUCT_AVG_PRICE_CATEGORY_SUMMARY, description = Messages.PRODUCT_AVG_PRICE_CATEGORY_DESCRIPTION)
    public ResponseEntity<Map<String, Double>> averagePricePerCategory() {
        return ResponseEntity.ok(productService.getAveragePricePerCategory());
    }

    @GetMapping("/price-range")
    @Operation(summary = Messages.PRODUCT_PRICE_RANGE_SUMMARY, description = Messages.PRODUCT_PRICE_RANGE_DESCRIPTION)
    public ResponseEntity<List<ProductResponseDTO>> productsByPriceRange(@RequestParam Double min, @RequestParam Double max) {
        return ResponseEntity.ok(productService.getProductsByPriceRange(min, max));
    }
}