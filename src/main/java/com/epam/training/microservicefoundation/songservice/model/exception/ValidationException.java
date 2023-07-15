package com.epam.training.microservicefoundation.songservice.model.exception;

public class ValidationException extends RuntimeException {
  public ValidationException(String message) {
    super(message);
  }

  public ValidationException(String message, Throwable error) {
    super(message, error);
  }
}
