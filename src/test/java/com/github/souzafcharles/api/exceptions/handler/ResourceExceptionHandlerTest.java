package com.github.souzafcharles.api.exceptions.handler;

import com.github.souzafcharles.api.exceptions.custom.DatabaseException;
import com.github.souzafcharles.api.exceptions.custom.DuplicateEmailException;
import com.github.souzafcharles.api.exceptions.custom.ResourceNotFoundException;
import com.github.souzafcharles.api.exceptions.model.StandardError;
import com.github.souzafcharles.api.utils.Messages;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResourceExceptionHandlerTest {

    private ResourceExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        // Arrange
        MockitoAnnotations.openMocks(this);
        handler = new ResourceExceptionHandler();
    }

    @Test
    void handleNotFoundShouldReturnStandardErrorWithNotFoundStatus() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/products/99");
        ResourceNotFoundException exception = new ResourceNotFoundException("99");

        // Act
        ResponseEntity<StandardError> response = handler.handleNotFound(exception, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals(Messages.ERROR_RESOURCE_NOT_FOUND, response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("99"));
        assertEquals("/products/99", response.getBody().getPath());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleDuplicateEmailShouldReturnStandardErrorWithBadRequest() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/users");
        DuplicateEmailException exception = new DuplicateEmailException("email@example.com");

        // Act
        ResponseEntity<StandardError> response = handler.handleDuplicateEmail(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Messages.ERROR_DUPLICATE_EMAIL, response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("email@example.com"));
        assertEquals("/users", response.getBody().getPath());
    }

    @Test
    void handleDatabaseExceptionShouldReturnStandardErrorWithBadRequest() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/database");
        DatabaseException exception = new DatabaseException("Foreign key violation");

        // Act
        ResponseEntity<StandardError> response = handler.handleDatabaseException(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Messages.ERROR_DATABASE, response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Foreign key violation"));
        assertEquals("/database", response.getBody().getPath());
    }

    @Test
    void handleGenericExceptionShouldReturnStandardErrorWithInternalServerError() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/any-endpoint");
        Exception exception = new Exception("Unexpected error");

        // Act
        ResponseEntity<StandardError> response = handler.handleGenericException(exception, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Messages.ERROR_GENERIC, response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("Unexpected error"));
        assertEquals("/any-endpoint", response.getBody().getPath());
    }
}