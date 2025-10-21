package com.github.souzafcharles.api.endpoint.user.initializer;

import com.github.souzafcharles.api.client.FakeStoreClient;
import com.github.souzafcharles.api.endpoint.user.model.dto.UserResponseDTO;
import com.github.souzafcharles.api.endpoint.user.model.entity.User;
import com.github.souzafcharles.api.endpoint.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.stream.StreamSupport;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class UserDataInitializerTest {

    private UserRepository userRepository;
    private FakeStoreClient fakeStoreClient;
    private UserDataInitializer initializer;

    @BeforeEach
    void setUp() {
        // Arrange
        userRepository = mock(UserRepository.class);
        fakeStoreClient = mock(FakeStoreClient.class);
        initializer = new UserDataInitializer(userRepository, fakeStoreClient);
    }

    @Test
    void initShouldNotSaveWhenRepositoryNotEmpty() {
        // Arrange
        when(userRepository.count()).thenReturn(3L);

        // Act
        initializer.init();

        // Assert
        verify(fakeStoreClient, never()).getAllUsers();
        verify(userRepository, never()).saveAll(anyList());
    }

    @Test
    void initShouldNotSaveWhenFakeStoreReturnsNull() {
        // Arrange
        when(userRepository.count()).thenReturn(0L);
        when(fakeStoreClient.getAllUsers()).thenReturn(Mono.justOrEmpty(null));

        // Act
        initializer.init();

        // Assert
        verify(fakeStoreClient, times(1)).getAllUsers();
        verify(userRepository, never()).saveAll(anyList());
    }

    @Test
    void initShouldNotSaveWhenFakeStoreReturnsEmptyArray() {
        // Arrange
        when(userRepository.count()).thenReturn(0L);
        when(fakeStoreClient.getAllUsers()).thenReturn(Mono.just(new UserResponseDTO[0]));

        // Act
        initializer.init();

        // Assert
        verify(fakeStoreClient, times(1)).getAllUsers();
        verify(userRepository, never()).saveAll(anyList());
    }

    @Test
    void initShouldSaveUsersWhenRepositoryIsEmpty() {
        // Arrange
        when(userRepository.count()).thenReturn(0L);
        UserResponseDTO dto1 = new UserResponseDTO("u1", "Alice", "alice@example.com");
        UserResponseDTO dto2 = new UserResponseDTO("u2", "Bob", "bob@example.com");
        when(fakeStoreClient.getAllUsers()).thenReturn(Mono.just(new UserResponseDTO[]{dto1, dto2}));

        // Act
        initializer.init();

        // Assert
        verify(fakeStoreClient, times(1)).getAllUsers();
        verify(userRepository, times(1)).saveAll(argThat(iterable ->
                StreamSupport.stream(iterable.spliterator(), false)
                        .anyMatch(u -> u.getId().equals("u1") && u.getUsername().equals("Alice"))
                        &&
                        StreamSupport.stream(iterable.spliterator(), false)
                                .anyMatch(u -> u.getId().equals("u2") && u.getUsername().equals("Bob"))
        ));
    }
}