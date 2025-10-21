package com.github.souzafcharles.api.endpoint.cart.controller;

import com.github.souzafcharles.api.endpoint.cart.model.dto.CartRequestDTO;
import com.github.souzafcharles.api.endpoint.cart.model.dto.CartResponseDTO;
import com.github.souzafcharles.api.endpoint.cart.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private PagedResourcesAssembler<CartResponseDTO> assembler;

    @InjectMocks
    private CartController cartController;

    private CartResponseDTO cartDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartDTO = new CartResponseDTO("c1", "u1", List.of());
    }

    @Test
    void getAllShouldReturnNoContentWhenPageIsEmpty() {
        // Arrange
        Page<CartResponseDTO> emptyPage = new PageImpl<>(List.of());
        when(cartService.getAllCarts(any(PageRequest.class))).thenReturn(emptyPage);

        // Act
        ResponseEntity<PagedModel<EntityModel<CartResponseDTO>>> response =
                cartController.getAll(PageRequest.of(0, 10), assembler);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getAllShouldReturnPagedModelWhenPageHasData() {
        // Arrange
        Page<CartResponseDTO> page = new PageImpl<>(List.of(cartDTO));
        when(cartService.getAllCarts(any(Pageable.class))).thenReturn(page);

        PagedModel<EntityModel<CartResponseDTO>> pagedModel =
                PagedModel.of(List.of(EntityModel.of(cartDTO)), new PagedModel.PageMetadata(1, 0, 1));

        when(assembler.<EntityModel<CartResponseDTO>>toModel(eq(page),
                ArgumentMatchers.<org.springframework.hateoas.server.RepresentationModelAssembler<CartResponseDTO, EntityModel<CartResponseDTO>>>any()))
                .thenReturn(pagedModel);

        // Act
        ResponseEntity<PagedModel<EntityModel<CartResponseDTO>>> response =
                cartController.getAll(Pageable.unpaged(), assembler);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
    }


    @Test
    void getByIdShouldReturnCart() {
        // Arrange
        when(cartService.getCartById("c1")).thenReturn(cartDTO);

        // Act
        ResponseEntity<CartResponseDTO> response = cartController.getById("c1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("c1", response.getBody().id());
    }

    @Test
    void createShouldReturnCreatedCart() {
        // Arrange
        CartRequestDTO requestDTO = new CartRequestDTO("u1", List.of());
        when(cartService.createCart(requestDTO)).thenReturn(cartDTO);

        // Act
        ResponseEntity<CartResponseDTO> response = cartController.create(requestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("c1", response.getBody().id());
    }

    @Test
    void updateShouldReturnUpdatedCart() {
        // Arrange
        CartRequestDTO requestDTO = new CartRequestDTO("u1", List.of());
        when(cartService.updateCart("c1", requestDTO)).thenReturn(cartDTO);

        // Act
        ResponseEntity<CartResponseDTO> response = cartController.update("c1", requestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("c1", response.getBody().id());
    }

    @Test
    void deleteShouldReturnNoContent() {
        // Arrange
        doNothing().when(cartService).deleteCart("c1");

        // Act
        ResponseEntity<Void> response = cartController.delete("c1");

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService, times(1)).deleteCart("c1");
    }

    @Test
    void getByUserIdShouldReturnCarts() {
        // Arrange
        when(cartService.getCartsByUserId("u1")).thenReturn(List.of(cartDTO));

        // Act
        ResponseEntity<List<CartResponseDTO>> response = cartController.getByUserId("u1");

        // Assert
        assertEquals(1, response.getBody().size());
        assertEquals("c1", response.getBody().get(0).id());
    }

    @Test
    void getByProductIdShouldReturnCarts() {
        // Arrange
        when(cartService.getCartsByProductId("p1")).thenReturn(List.of(cartDTO));

        // Act
        ResponseEntity<List<CartResponseDTO>> response = cartController.getByProductId("p1");

        // Assert
        assertEquals(1, response.getBody().size());
        assertEquals("c1", response.getBody().get(0).id());
    }

    @Test
    void getTotalProductsForUserShouldReturnValue() {
        // Arrange
        when(cartService.getTotalProductsForUser("u1")).thenReturn(5L);

        // Act
        ResponseEntity<Long> response = cartController.getTotalProductsForUser("u1");

        // Assert
        assertEquals(5L, response.getBody());
    }

    @Test
    void getCartsWithTotalValueGreaterThanShouldReturnCarts() {
        // Arrange
        when(cartService.getCartsWithTotalValueGreaterThan(100.0)).thenReturn(List.of(cartDTO));

        // Act
        ResponseEntity<List<CartResponseDTO>> response = cartController.getCartsWithTotalValueGreaterThan(100.0);

        // Assert
        assertEquals(1, response.getBody().size());
        assertEquals("c1", response.getBody().get(0).id());
    }

    @Test
    void getAllShouldCoverAssemblerLambda() {
        // Arrange
        Page<CartResponseDTO> page = new PageImpl<>(List.of(cartDTO));
        when(cartService.getAllCarts(any(Pageable.class))).thenReturn(page);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<org.springframework.hateoas.server.RepresentationModelAssembler<CartResponseDTO, EntityModel<CartResponseDTO>>> captor =
                ArgumentCaptor.forClass(org.springframework.hateoas.server.RepresentationModelAssembler.class);

        PagedModel<EntityModel<CartResponseDTO>> pagedModel =
                PagedModel.of(List.of(EntityModel.of(cartDTO)), new PagedModel.PageMetadata(1, 0, 1));
        when(assembler.toModel(eq(page), captor.capture())).thenReturn(pagedModel);

        // Act
        ResponseEntity<PagedModel<EntityModel<CartResponseDTO>>> response =
                cartController.getAll(Pageable.unpaged(), assembler);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        var lambda = captor.getValue();
        EntityModel<CartResponseDTO> entity = lambda.toModel(cartDTO);
        assertTrue(entity.hasLink("self"));
    }

}