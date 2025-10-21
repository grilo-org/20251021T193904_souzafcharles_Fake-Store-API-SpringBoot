package com.github.souzafcharles.api.exceptions.custom;

import com.github.souzafcharles.api.utils.Messages;

public class DatabaseException extends RuntimeException {
    public DatabaseException(String message) {
        super(String.format(Messages.EXCEPTION_DATABASE, message));
    }
}