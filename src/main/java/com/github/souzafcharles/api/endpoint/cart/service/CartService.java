package com.github.souzafcharles.api.endpoint.cart.service;

import com.github.souzafcharles.api.endpoint.cart.model.dto.CartRequestDTO;
import com.github.souzafcharles.api.endpoint.cart.model.dto.CartResponseDTO;
import com.github.souzafcharles.api.endpoint.cart.model.entity.Cart;
import com.github.souzafcharles.api.endpoint.cart.repository.CartRepository;
import com.github.souzafcharles.api.endpoint.cartproduct.model.entity.CartProduct;
import com.github.souzafcharles.api.endpoint.product.repository.ProductRepository;
import com.github.souzafcharles.api.endpoint.user.repository.UserRepository;
import com.github.souzafcharles.api.exceptions.custom.DatabaseException;
import com.github.souzafcharles.api.exceptions.custom.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       UserRepository userRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public Page<CartResponseDTO> getAllCarts(Pageable pageable) {
        List<CartResponseDTO> allCarts = cartRepository.findAll().stream()
                .map(CartResponseDTO::new)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allCarts.size());
        return new PageImpl<>(allCarts.subList(start, end), pageable, allCarts.size());
    }

    public CartResponseDTO getCartById(String id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forCart(id));
        return new CartResponseDTO(cart);
    }

    public CartResponseDTO createCart(CartRequestDTO dto) {
        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> ResourceNotFoundException.forUser(dto.userId()));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setCartProducts(buildCartProducts(cart, dto));

        return new CartResponseDTO(cartRepository.save(cart));
    }

    public CartResponseDTO updateCart(String id, CartRequestDTO dto) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forCart(id));

        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> ResourceNotFoundException.forUser(dto.userId()));

        cart.setUser(user);
        cart.setCartProducts(buildCartProducts(cart, dto));

        return new CartResponseDTO(cartRepository.save(cart));
    }

    private List<CartProduct> buildCartProducts(Cart cart, CartRequestDTO dto) {
        return dto.products().stream()
                .map(p -> productRepository.findById(p.productId())
                        .map(product -> {
                            CartProduct cp = new CartProduct();
                            cp.setCart(cart);
                            cp.setProduct(product);
                            cp.setQuantity(p.quantity());
                            return cp;
                        })
                        .orElseThrow(() -> ResourceNotFoundException.forProduct(p.productId()))
                ).toList();
    }

    public void deleteCart(String id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.forCart(id));
        try {
            cartRepository.delete(cart);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public List<CartResponseDTO> getCartsByUserId(String userId) {
        return cartRepository.findByUserId(userId).stream()
                .map(CartResponseDTO::new)
                .toList();
    }

    public List<CartResponseDTO> getCartsByProductId(String productId) {
        return cartRepository.findByCartProductsProductId(productId).stream()
                .map(CartResponseDTO::new)
                .toList();
    }

    public long getTotalProductsForUser(String userId) {
        return cartRepository.findByUserId(userId).stream()
                .flatMap(c -> c.getCartProducts().stream())
                .mapToLong(cp -> cp.getQuantity() != null ? cp.getQuantity() : 0)
                .sum();
    }

    public List<CartResponseDTO> getCartsWithTotalValueGreaterThan(Double minTotal) {
        return cartRepository.findAll().stream()
                .filter(cart -> cart.getCartProducts().stream()
                        .mapToDouble(cp -> cp.getQuantity() * cp.getProduct().getPrice())
                        .sum() > minTotal)
                .map(CartResponseDTO::new)
                .toList();
    }
}