package com.epam.training.microservicefoundation.songservice.model;

public class SongNotFoundException extends RuntimeException {
    public SongNotFoundException(String message) {
        super(message);
    }

    public SongNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
