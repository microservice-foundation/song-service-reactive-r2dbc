package com.epam.training.microservicefoundation.songservice.handler.impl;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.epam.training.microservicefoundation.songservice.model.APIError;
import com.epam.training.microservicefoundation.songservice.model.exception.EntityExistsException;
import com.epam.training.microservicefoundation.songservice.model.exception.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public class SongExceptionHandler extends AbstractErrorWebExceptionHandler {
  private final Map<Class<? extends Throwable>, Function<Throwable, Mono<ServerResponse>>> exceptionToHandlers;

  private static final HttpStatus DEFAULT_HTTP_STATUS = INTERNAL_SERVER_ERROR;
  private static final String DEFAULT_MESSAGE = "Hmm.. there is an unknown issue occurred";

  public SongExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext) {
    super(errorAttributes, resources, applicationContext);
    exceptionToHandlers = new HashMap<>();
    registerExceptionHandler(List.of(IllegalArgumentException.class, NumberFormatException.class, ResponseStatusException.class),
        BAD_REQUEST, "Invalid request");
    registerExceptionHandler(EntityNotFoundException.class, NOT_FOUND, "Song is not found");
    registerExceptionHandler(EntityExistsException.class, BAD_REQUEST, "Song is already existed");
    registerExceptionHandler(IllegalStateException.class, INTERNAL_SERVER_ERROR, "Internal error has happened");
  }

  private void registerExceptionHandler(List<Class<? extends Throwable>> exceptionClasses, HttpStatus status, String message) {
    exceptionClasses.forEach(exceptionClass -> registerExceptionHandler(exceptionClass, status, message));
  }

  private void registerExceptionHandler(Class<? extends Throwable> exceptionClass, HttpStatus status, String message) {
    exceptionToHandlers.put(exceptionClass, exception -> handleException(exception, status, message));
  }

  private Mono<ServerResponse> handleException(Throwable exception, HttpStatus status, String message) {
    return ServerResponse
        .status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new APIError(status, message, exception)));
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), request -> {
      Throwable error = getError(request);
      Function<Throwable, Mono<ServerResponse>> exceptionHandler = exceptionToHandlers.getOrDefault(error.getClass(),
          throwable -> handleException(throwable, DEFAULT_HTTP_STATUS, DEFAULT_MESSAGE));

      return exceptionHandler.apply(error);
    });
  }
}
