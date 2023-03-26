package com.epam.training.microservicefoundation.songservice.api;

import com.epam.training.microservicefoundation.songservice.model.APIError;
import com.epam.training.microservicefoundation.songservice.model.SongNotFoundException;
import java.util.HashMap;
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
import reactor.core.publisher.Mono;


public class SongExceptionHandler extends AbstractErrorWebExceptionHandler {
  private final Map<Class<? extends Throwable>, Function<Throwable, Mono<ServerResponse>>> exceptionToHandlers;

  public SongExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext) {
    super(errorAttributes, resources, applicationContext);
    exceptionToHandlers = new HashMap<>();
    Function<Throwable, Mono<ServerResponse>> invalidRequest = throwable -> ServerResponse
        .status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new APIError(HttpStatus.BAD_REQUEST, "Invalid request", throwable)));

    exceptionToHandlers.put(IllegalArgumentException.class, invalidRequest);
    exceptionToHandlers.put(NumberFormatException.class, invalidRequest);

    exceptionToHandlers.put(SongNotFoundException.class, throwable -> ServerResponse
        .status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new APIError(HttpStatus.NOT_FOUND, "Song metadata is not found", throwable))));

    exceptionToHandlers.put(IllegalStateException.class, throwable -> ServerResponse
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new APIError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error has happened",
            throwable))));

  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(),
        request -> {
          Throwable error = getError(request);
          if (exceptionToHandlers.containsKey(error.getClass())) {
            return exceptionToHandlers.get(error.getClass()).apply(error);
          }
          return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue(BodyInserters.fromValue(new APIError(HttpStatus.INTERNAL_SERVER_ERROR,
                  "Hmm.. there is an unknown issue occurred", error)));
        });
  }
}
