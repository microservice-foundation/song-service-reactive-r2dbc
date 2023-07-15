package com.epam.training.microservicefoundation.songservice.handler.impl;

import com.epam.training.microservicefoundation.songservice.service.implementation.SongServiceImpl;
import com.epam.training.microservicefoundation.songservice.validator.RequestBodyValidator;
import com.epam.training.microservicefoundation.songservice.validator.RequestQueryParamValidator;
import com.epam.training.microservicefoundation.songservice.model.dto.DeleteSongDTO;
import com.epam.training.microservicefoundation.songservice.model.dto.GetSongDTO;
import com.epam.training.microservicefoundation.songservice.model.dto.SaveSongDTO;
import com.epam.training.microservicefoundation.songservice.service.SongService;
import java.net.URI;
import java.util.Arrays;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
public class SongHandler {
  private static final Logger log = Loggers.getLogger(SongHandler.class);
  private final SongService service;
  private final RequestBodyValidator<SaveSongDTO> saveSongDTORequestBodyValidator;
  private final RequestQueryParamValidator queryParamValidator;

  public SongHandler(SongService service, RequestBodyValidator<SaveSongDTO> saveSongDTORequestBodyValidator,
      RequestQueryParamValidator queryParamValidator) {
    this.service = service;
    this.saveSongDTORequestBodyValidator = saveSongDTORequestBodyValidator;
    this.queryParamValidator = queryParamValidator;
  }

  public Mono<ServerResponse> save(ServerRequest request) {
    log.info("Handling save request.");
    Mono<SaveSongDTO> validBody = saveSongDTORequestBodyValidator.validateBody(request.bodyToMono(SaveSongDTO.class));
    return ServerResponse.created(URI.create(request.path()))
        .body(service.save(validBody), GetSongDTO.class);
  }

  public Mono<ServerResponse> update(ServerRequest request) {
    log.info("Handling update request.");
    final long id = Long.parseLong(request.pathVariable("id"));
    Mono<SaveSongDTO> validBody = saveSongDTORequestBodyValidator.validateBody(request.bodyToMono(SaveSongDTO.class));
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(service.update(id, validBody), GetSongDTO.class);
  }

  public Mono<ServerResponse> getById(ServerRequest request) {
    log.info("Handling get-song-by-id request.");
    long id = Long.parseLong(request.pathVariable("id"));
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(service.getById(id), GetSongDTO.class);
  }

  public Mono<ServerResponse> deleteByIds(ServerRequest request, final String queryParam) {
    log.info("Handling delete-songs-by-ids request.");
    final String validQueryParamValue = queryParamValidator.validateQueryParam(request.queryParam(queryParam), queryParam);
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(service.deleteByIds(getIds(validQueryParamValue)), DeleteSongDTO.class);
  }

  public Mono<ServerResponse> deleteByResourceIds(ServerRequest request, final String queryParam) {
    log.info("Handling delete-songs-by-request-ids requst.");
    final String validQueryParamValue = queryParamValidator.validateQueryParam(request.queryParam(queryParam), queryParam);
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(service.deleteByResourceIds(getIds(validQueryParamValue)), DeleteSongDTO.class);
  }

  private Long[] getIds(String paramValue) {
    return Arrays.stream(paramValue.split(","))
        .map(String::trim)
        .map(Long::valueOf)
        .toArray(Long[]::new);
  }
}
