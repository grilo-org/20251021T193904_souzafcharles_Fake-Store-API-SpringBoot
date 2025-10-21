package com.github.souzafcharles.api.exceptions.custom;

import com.github.souzafcharles.api.utils.Messages;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(Object email) {
        super(String.format(Messages.EXCEPTION_DUPLICATE_EMAIL, email));
    }
}