package com.github.souzafcharles.api.endpoint.user.controller;

import com.github.souzafcharles.api.endpoint.user.model.dto.UserRequestDTO;
import com.github.souzafcharles.api.endpoint.user.model.dto.UserResponseDTO;
import com.github.souzafcharles.api.endpoint.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PagedResourcesAssembler<UserResponseDTO> assembler;

    @InjectMocks
    private UserController userController;

    private UserResponseDTO userDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDTO = new UserResponseDTO("u1", "Alice", "alice@example.com");
    }

    @Test
    void getAllShouldReturnNoContentWhenPageIsEmpty() {
        // Arrange
        Page<UserResponseDTO> emptyPage = new PageImpl<>(Collections.emptyList());
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(emptyPage);

        // Act
        ResponseEntity<PagedModel<EntityModel<UserResponseDTO>>> response =
                userController.getAll(Pageable.unpaged(), assembler);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getAllShouldReturnPagedModelWhenPageHasData() {
        // Arrange
        Page<UserResponseDTO> page = new PageImpl<>(List.of(userDTO));
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        PagedModel<EntityModel<UserResponseDTO>> pagedModel =
                PagedModel.of(List.of(EntityModel.of(userDTO)), new PagedModel.PageMetadata(1, 0, 1));

        when(assembler.<EntityModel<UserResponseDTO>>toModel(
                eq(page),
                ArgumentMatchers.<org.springframework.hateoas.server.RepresentationModelAssembler<UserResponseDTO, EntityModel<UserResponseDTO>>>any()
        )).thenReturn(pagedModel);

        // Act
        ResponseEntity<PagedModel<EntityModel<UserResponseDTO>>> response =
                userController.getAll(Pageable.unpaged(), assembler);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
    }


    @Test
    void getByIdShouldReturnUser() {
        // Arrange
        when(userService.getUserById("u1")).thenReturn(userDTO);

        // Act
        ResponseEntity<UserResponseDTO> response = userController.getById("u1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Alice", response.getBody().username());
    }

    @Test
    void createShouldReturnCreatedUser() {
        // Arrange
        UserRequestDTO requestDTO = new UserRequestDTO("Alice", "alice@example.com", "123456");
        when(userService.createUser(requestDTO)).thenReturn(userDTO);

        // Act
        ResponseEntity<UserResponseDTO> response = userController.create(requestDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Alice", response.getBody().username());
    }

    @Test
    void updateShouldReturnUpdatedUser() {
        // Arrange
        UserRequestDTO requestDTO = new UserRequestDTO("AliceUpdated", "alice@example.com", "123456");
        when(userService.updateUser("u1", requestDTO)).thenReturn(userDTO);

        // Act
        ResponseEntity<UserResponseDTO> response = userController.update("u1", requestDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Alice", response.getBody().username());
    }

    @Test
    void deleteShouldReturnNoContent() {
        // Arrange
        doNothing().when(userService).deleteUser("u1");

        // Act
        ResponseEntity<Void> response = userController.delete("u1");

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser("u1");
    }

    @Test
    void searchByUsernameShouldReturnUsers() {
        // Arrange
        when(userService.searchUsersByUsername("Ali")).thenReturn(List.of(userDTO));

        // Act
        ResponseEntity<List<UserResponseDTO>> response = userController.searchByUsername("Ali");

        // Assert
        assertEquals(1, response.getBody().size());
        assertEquals("Alice", response.getBody().get(0).username());
    }

    @Test
    void getByUsernameShouldReturnUser() {
        // Arrange
        when(userService.getUserByUsername("Alice")).thenReturn(userDTO);

        // Act
        ResponseEntity<UserResponseDTO> response = userController.getByUsername("Alice");

        // Assert
        assertEquals("Alice", response.getBody().username());
    }

    @Test
    void getByEmailShouldReturnUser() {
        // Arrange
        when(userService.getUserByEmail("alice@example.com")).thenReturn(userDTO);

        // Act
        ResponseEntity<UserResponseDTO> response = userController.getByEmail("alice@example.com");

        // Assert
        assertEquals("alice@example.com", response.getBody().email());
    }

    @Test
    void countUsersShouldReturnTotal() {
        // Arrange
        when(userService.countUsers()).thenReturn(5L);

        // Act
        ResponseEntity<Long> response = userController.countUsers();

        // Assert
        assertEquals(5L, response.getBody());
    }

    @Test
    void getAllShouldCoverAssemblerLambda() {
        // Arrange
        Page<UserResponseDTO> page = new PageImpl<>(List.of(userDTO));
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<org.springframework.hateoas.server.RepresentationModelAssembler<UserResponseDTO, EntityModel<UserResponseDTO>>> captor =
                ArgumentCaptor.forClass(org.springframework.hateoas.server.RepresentationModelAssembler.class);

        PagedModel<EntityModel<UserResponseDTO>> pagedModel =
                PagedModel.of(List.of(EntityModel.of(userDTO)), new PagedModel.PageMetadata(1, 0, 1));
        when(assembler.toModel(eq(page), captor.capture())).thenReturn(pagedModel);

        // Act
        ResponseEntity<PagedModel<EntityModel<UserResponseDTO>>> response =
                userController.getAll(Pageable.unpaged(), assembler);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        var lambda = captor.getValue();
        EntityModel<UserResponseDTO> entity = lambda.toModel(userDTO);
        assertTrue(entity.hasLink("self"));
    }
}