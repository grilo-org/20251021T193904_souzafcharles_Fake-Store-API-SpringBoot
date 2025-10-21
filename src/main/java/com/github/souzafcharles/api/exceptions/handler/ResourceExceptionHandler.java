package com.github.souzafcharles.api.exceptions.handler;

import com.github.souzafcharles.api.exceptions.custom.*;
import com.github.souzafcharles.api.exceptions.model.StandardError;
import com.github.souzafcharles.api.utils.Messages;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    private ResponseEntity<StandardError> buildResponse(
            Exception e, String error, HttpStatus status, HttpServletRequest request) {

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                error,
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> handleNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        return buildResponse(e, Messages.ERROR_RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<StandardError> handleDuplicateEmail(DuplicateEmailException e, HttpServletRequest request) {
        return buildResponse(e, Messages.ERROR_DUPLICATE_EMAIL, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> handleDatabaseException(DatabaseException e, HttpServletRequest request) {
        return buildResponse(e, Messages.ERROR_DATABASE, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> handleGenericException(Exception e, HttpServletRequest request) {
        return buildResponse(e, Messages.ERROR_GENERIC, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
