package com.epam.training.microservicefoundation.songservice.domain.exception;

public class EntityExistsException extends RuntimeException {
  public EntityExistsException(String message, Throwable error) {
    super(message, error);
  }
}
