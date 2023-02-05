package com.epam.training.microservicefoundation.songservice.api;

import com.epam.training.microservicefoundation.songservice.model.APIError;
import com.epam.training.microservicefoundation.songservice.model.SongNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SongExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildResponseEntity(new APIError(HttpStatus.BAD_REQUEST, "Invalid request", ex));
    }

    @ExceptionHandler(SongNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(SongNotFoundException ex) {
        return buildResponseEntity(new APIError(HttpStatus.NOT_FOUND, "Song metadata not found", ex));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleBusinessException(IllegalStateException ex) {
        return buildResponseEntity(new APIError(HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error happened", ex));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        String error = "Malformed JSON request";
        return buildResponseEntity(new APIError(HttpStatus.BAD_REQUEST, error, ex));
    }

    private ResponseEntity<Object> buildResponseEntity(APIError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
