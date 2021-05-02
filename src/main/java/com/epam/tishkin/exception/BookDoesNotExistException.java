package com.epam.tishkin.exception;

public class BookDoesNotExistException extends Exception {

    public BookDoesNotExistException(String message) {
        super(message);
    }
}
