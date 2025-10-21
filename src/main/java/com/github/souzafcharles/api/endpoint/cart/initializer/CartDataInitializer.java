package com.github.souzafcharles.api.endpoint.cart.initializer;

import com.github.souzafcharles.api.endpoint.cart.model.entity.Cart;
import com.github.souzafcharles.api.endpoint.cartproduct.model.entity.CartProduct;
import com.github.souzafcharles.api.endpoint.cart.model.dto.CartResponseDTO;
import com.github.souzafcharles.api.endpoint.cart.repository.CartRepository;
import com.github.souzafcharles.api.client.FakeStoreClient;
import com.github.souzafcharles.api.endpoint.product.model.entity.Product;
import com.github.souzafcharles.api.endpoint.product.repository.ProductRepository;
import com.github.souzafcharles.api.endpoint.user.model.entity.User;
import com.github.souzafcharles.api.endpoint.user.repository.UserRepository;
import com.github.souzafcharles.api.utils.Messages;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.DependsOn;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
@DependsOn({"userDataInitializer", "productDataInitializer"})
public class CartDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(CartDataInitializer.class);

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FakeStoreClient fakeStoreClient;

    public CartDataInitializer(CartRepository cartRepository,
                               ProductRepository productRepository,
                               UserRepository userRepository,
                               FakeStoreClient fakeStoreClient) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.fakeStoreClient = fakeStoreClient;
    }

    @PostConstruct
    public void init() {
        if (cartRepository.count() > 0) {
            log.info(Messages.CART_ALREADY_INITIALIZED);
            return;
        }

        CartResponseDTO[] carts = fakeStoreClient.getAllCarts().block();
        if (carts == null || carts.length == 0) {
            log.warn(Messages.CART_NO_RETURNED);
            return;
        }

        List<Cart> entities = Arrays.stream(carts)
                .map(dto -> {
                    User user = userRepository.findById(dto.userId()).orElse(null);
                    if (user == null) {
                        log.warn(Messages.CART_IGNORED_USER_NOT_FOUND, dto.id(), dto.userId());
                        return null;
                    }

                    Cart cart = new Cart();
                    cart.setId(dto.id());
                    cart.setUser(user);

                    List<CartProduct> cartProducts = dto.products().stream()
                            .map(p -> {
                                Product product = productRepository.findById(p.productId()).orElse(null);
                                if (product == null) {
                                    log.warn(Messages.CART_PRODUCT_IGNORED, dto.id(), p.productId());
                                    return null;
                                }
                                CartProduct cartProduct = new CartProduct();
                                cartProduct.setCart(cart);
                                cartProduct.setProduct(product);
                                cartProduct.setQuantity(p.quantity());
                                return cartProduct;
                            })
                            .filter(Objects::nonNull)
                            .toList();

                    cart.setCartProducts(cartProducts);
                    return cart;
                })
                .filter(Objects::nonNull)
                .toList();

        if (!entities.isEmpty()) {
            cartRepository.saveAll(entities);
            log.info(Messages.CART_SAVED_SUCCESS, entities.size());
        } else {
            log.warn(Messages.CART_NO_VALID);
        }
    }
}