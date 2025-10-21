package com.github.souzafcharles.api.endpoint.user.service;

import com.github.souzafcharles.api.endpoint.user.model.dto.UserRequestDTO;
import com.github.souzafcharles.api.endpoint.user.model.entity.User;
import com.github.souzafcharles.api.endpoint.user.repository.UserRepository;
import com.github.souzafcharles.api.exceptions.custom.DatabaseException;
import com.github.souzafcharles.api.exceptions.custom.DuplicateEmailException;
import com.github.souzafcharles.api.exceptions.custom.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId("u1");
        user.setUsername("Alice");
        user.setEmail("alice@example.com");
        user.setPassword("123456");

        anotherUser = new User();
        anotherUser.setId("u2");
        anotherUser.setUsername("Bob");
        anotherUser.setEmail("bob@example.com");
        anotherUser.setPassword("654321");
    }

    @Test
    void getAllUsersShouldReturnPagedResult() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(user, anotherUser));

        // Act
        var page = userService.getAllUsers(PageRequest.of(0, 10));

        // Assert
        assertEquals(2, page.getTotalElements());
        assertEquals("Alice", page.getContent().get(0).username());
        assertEquals("Bob", page.getContent().get(1).username());
    }

    @Test
    void getUserByIdShouldReturnUser() {
        // Arrange
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));

        // Act
        var response = userService.getUserById("u1");

        // Assert
        assertEquals("Alice", response.username());
        assertEquals("alice@example.com", response.email());
    }

    @Test
    void getUserByIdShouldThrowException() {
        // Arrange
        when(userRepository.findById("u99")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById("u99"));
    }

    @Test
    void createUserShouldSaveUser() {
        // Arrange
        UserRequestDTO dto = new UserRequestDTO("Charlie", "charlie@example.com", "pass");
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        User savedUser = new User();
        savedUser.setId("u3");
        savedUser.setUsername(dto.username());
        savedUser.setEmail(dto.email());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        var response = userService.createUser(dto);

        // Assert
        assertEquals("Charlie", response.username());
        assertEquals("u3", response.id());
    }

    @Test
    void createUserShouldThrowDuplicateEmailException() {
        // Arrange
        UserRequestDTO dto = new UserRequestDTO("Alice2", "alice@example.com", "pass");
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> userService.createUser(dto));
    }

    @Test
    void createUserShouldThrowDatabaseException() {
        // Arrange
        UserRequestDTO dto = new UserRequestDTO("Charlie", "charlie@example.com", "pass");
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("DB error"));

        // Act & Assert
        assertThrows(DatabaseException.class, () -> userService.createUser(dto));
    }

    @Test
    void updateUserShouldUpdateExistingUser() {
        // Arrange
        UserRequestDTO dto = new UserRequestDTO("AliceUpdated", "alice@example.com", "newpass");
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        var response = userService.updateUser("u1", dto);

        // Assert
        assertEquals("AliceUpdated", response.username());
    }

    @Test
    void updateUserShouldThrowResourceNotFoundException() {
        // Arrange
        UserRequestDTO dto = new UserRequestDTO("NonExistent", "nonexistent@example.com", "pass");
        when(userRepository.findById("u99")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser("u99", dto));
    }

    @Test
    void updateUserShouldThrowDuplicateEmailException() {
        // Arrange
        UserRequestDTO dto = new UserRequestDTO("BobUpdated", "bob@example.com", "newpass");
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(anotherUser));

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> userService.updateUser("u1", dto));
    }

    @Test
    void updateUserShouldThrowDatabaseException() {
        // Arrange
        UserRequestDTO dto = new UserRequestDTO("AliceUpdated", "alice@example.com", "newpass");
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("DB error"));

        // Act & Assert
        assertThrows(DatabaseException.class, () -> userService.updateUser("u1", dto));
    }

    @Test
    void deleteUserShouldRemoveUser() {
        // Arrange
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        // Act
        assertDoesNotThrow(() -> userService.deleteUser("u1"));

        // Assert
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUserShouldThrowResourceNotFoundException() {
        // Arrange
        when(userRepository.findById("u99")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser("u99"));
    }

    @Test
    void deleteUserShouldThrowDatabaseException() {
        // Arrange
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        doThrow(new DataIntegrityViolationException("DB error")).when(userRepository).delete(user);

        // Act & Assert
        assertThrows(DatabaseException.class, () -> userService.deleteUser("u1"));
    }

    @Test
    void getUserByUsernameShouldReturnUser() {
        // Arrange
        when(userRepository.findByUsername("Alice")).thenReturn(Optional.of(user));

        // Act
        var response = userService.getUserByUsername("Alice");

        // Assert
        assertEquals("Alice", response.username());
    }

    @Test
    void getUserByUsernameShouldThrowResourceNotFoundException() {
        // Arrange
        when(userRepository.findByUsername("Unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername("Unknown"));
    }

    @Test
    void getUserByEmailShouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        // Act
        var response = userService.getUserByEmail("alice@example.com");

        // Assert
        assertEquals("alice@example.com", response.email());
    }

    @Test
    void getUserByEmailShouldThrowResourceNotFoundException() {
        // Arrange
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("unknown@example.com"));
    }

    @Test
    void searchUsersByUsernameShouldReturnMatchingUsers() {
        // Arrange
        when(userRepository.findByUsernameContainingIgnoreCase("Ali")).thenReturn(List.of(user));

        // Act
        var results = userService.searchUsersByUsername("Ali");

        // Assert
        assertEquals(1, results.size());
        assertEquals("Alice", results.get(0).username());
    }

    @Test
    void countUsersShouldReturnTotal() {
        // Arrange
        when(userRepository.countByUsernameIsNotNull()).thenReturn(5L);

        // Act
        long count = userService.countUsers();

        // Assert
        assertEquals(5L, count);
    }
}
