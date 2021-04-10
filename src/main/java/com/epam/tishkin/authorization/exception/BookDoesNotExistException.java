package com.epam.tishkin.authorization.exception;

public class BookDoesNotExistException extends Exception {

    public BookDoesNotExistException(String message) {
        super(message);
    }
}
