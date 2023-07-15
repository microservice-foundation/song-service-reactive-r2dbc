package com.epam.training.microservicefoundation.songservice.model;

import java.io.Serializable;
import org.springframework.http.HttpStatus;

public class APIError implements Serializable {
  private static final long serialVersionUID = 2023_07_15_15_55L;
  private HttpStatus status;
  private final long timestamp;
  private String message;
  private String debugMessage;

  private APIError() {
    timestamp = System.currentTimeMillis();
  }

  public APIError(HttpStatus status, Throwable ex) {
    this();
    this.message = "Unexpected error";
    this.status = status;
    this.debugMessage = ex.getLocalizedMessage();
  }

  public APIError(HttpStatus status, String message, Throwable ex) {
    this();
    this.status = status;
    this.message = message;
    this.debugMessage = ex.getLocalizedMessage();
  }

  public HttpStatus getStatus() {
    return status;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getMessage() {
    return message;
  }

  public String getDebugMessage() {
    return debugMessage;
  }
}
