package com.github.souzafcharles.api.endpoint.cart.controller;

import com.github.souzafcharles.api.endpoint.cart.model.dto.CartRequestDTO;
import com.github.souzafcharles.api.endpoint.cart.model.dto.CartResponseDTO;
import com.github.souzafcharles.api.endpoint.cart.service.CartService;
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

@RestController
@RequestMapping("/carts")
@Tag(name = "Cart API", description = Messages.CART_TAG_DESCRIPTION)
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = Messages.CART_GET_ALL_SUMMARY, description = Messages.CART_GET_ALL_DESCRIPTION)
    public ResponseEntity<PagedModel<EntityModel<CartResponseDTO>>> getAll(
            Pageable pageable,
            PagedResourcesAssembler<CartResponseDTO> assembler
    ) {
        var page = cartService.getAllCarts(pageable);
        var model = assembler.toModel(page, cart ->
                EntityModel.of(cart, WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(CartController.class).getById(cart.id())
                ).withSelfRel())
        );
        return page.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(model);
    }

    @GetMapping("/{id}")
    @Operation(summary = Messages.CART_GET_BY_ID_SUMMARY, description = Messages.CART_GET_BY_ID_DESCRIPTION)
    public ResponseEntity<CartResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(cartService.getCartById(id));
    }

    @PostMapping
    @Operation(summary = Messages.CART_CREATE_SUMMARY, description = Messages.CART_CREATE_DESCRIPTION)
    public ResponseEntity<CartResponseDTO> create(@RequestBody @Valid CartRequestDTO dto) {
        CartResponseDTO created = cartService.createCart(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = Messages.CART_UPDATE_SUMMARY, description = Messages.CART_UPDATE_DESCRIPTION)
    public ResponseEntity<CartResponseDTO> update(
            @PathVariable String id,
            @RequestBody @Valid CartRequestDTO dto
    ) {
        return ResponseEntity.ok(cartService.updateCart(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = Messages.CART_DELETE_SUMMARY, description = Messages.CART_DELETE_DESCRIPTION)
    public ResponseEntity<Void> delete(@PathVariable String id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = Messages.CART_BY_USER_SUMMARY, description = Messages.CART_BY_USER_DESCRIPTION)
    public ResponseEntity<List<CartResponseDTO>> getByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCartsByUserId(userId));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = Messages.CART_BY_PRODUCT_SUMMARY, description = Messages.CART_BY_PRODUCT_DESCRIPTION)
    public ResponseEntity<List<CartResponseDTO>> getByProductId(@PathVariable String productId) {
        return ResponseEntity.ok(cartService.getCartsByProductId(productId));
    }

    @GetMapping("/user/{userId}/total-products")
    @Operation(summary = Messages.CART_TOTAL_PRODUCTS_SUMMARY, description = Messages.CART_TOTAL_PRODUCTS_DESCRIPTION)
    public ResponseEntity<Long> getTotalProductsForUser(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getTotalProductsForUser(userId));
    }

    @GetMapping("/total-value")
    @Operation(summary = Messages.CART_TOTAL_VALUE_SUMMARY, description = Messages.CART_TOTAL_VALUE_DESCRIPTION)
    public ResponseEntity<List<CartResponseDTO>> getCartsWithTotalValueGreaterThan(@RequestParam Double minTotal) {
        return ResponseEntity.ok(cartService.getCartsWithTotalValueGreaterThan(minTotal));
    }
}