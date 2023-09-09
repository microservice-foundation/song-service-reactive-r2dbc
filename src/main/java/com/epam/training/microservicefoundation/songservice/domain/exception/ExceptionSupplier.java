package com.epam.training.microservicefoundation.songservice.domain.exception;

import com.epam.training.microservicefoundation.songservice.web.validator.QueryParamValidationErrors;
import java.util.function.Supplier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.server.ResponseStatusException;

public class ExceptionSupplier {
  private ExceptionSupplier() {}

  public static Supplier<EntityNotFoundException> entityNotFound(Class<?> entityClass, long id) {
    return () -> new EntityNotFoundException(String.format("%s with id=%d is not found", entityClass.getSimpleName(), id));
  }

  public static Supplier<EntityExistsException> entityAlreadyExists(Class<?> entityClass, Throwable error) {
    return () -> new EntityExistsException(String.format("%s with these parameters already exists", entityClass.getSimpleName()), error);
  }

  public static Supplier<ResponseStatusException> invalidRequest(Errors errors) {
    return () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.getAllErrors().toString());
  }

  public static Supplier<ResponseStatusException> invalidRequest(QueryParamValidationErrors errors) {
    return () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, errors.getAllErrors().toString());
  }
}
