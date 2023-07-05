package com.epam.training.microservicefoundation.songservice.api;

import com.epam.training.microservicefoundation.songservice.model.SongDTO;
import com.epam.training.microservicefoundation.songservice.model.SongMetadata;
import com.epam.training.microservicefoundation.songservice.service.SongService;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SongHandler {
  private final SongService service;

  @Autowired
  public SongHandler(SongService service) {
    this.service = service;
  }

  public Mono<ServerResponse> save(ServerRequest request) {
    return ServerResponse.created(URI.create(request.path()))
        .body(service.save(request.bodyToMono(SongMetadata.class)), SongDTO.class);
  }

  public Mono<ServerResponse> update(ServerRequest request) {
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(service.update(request.bodyToMono(SongMetadata.class)), SongMetadata.class);
  }

  public Mono<ServerResponse> deleteByIds(ServerRequest request) {
    Flux<Long> idsFlux = request
        .queryParam("id")
        .map(string -> Flux.fromArray(string.split(",")).map(Long::parseLong))
        .orElse(Flux.empty());

    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(service.deleteByIds(idsFlux), SongDTO.class);
  }

  public Mono<ServerResponse> getById(ServerRequest request) {
    long id = Long.parseLong(request.pathVariable("id"));
    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(service.getById(id), SongMetadata.class);
  }

  public Mono<ServerResponse> deleteByResourceIds(ServerRequest request) {
    Flux<Long> idsMono = request
        .queryParam("id")
        .map(string -> Flux.fromArray(string.split(",")).map(Long::parseLong))
        .orElse(Flux.empty());

    return ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(service.deleteByResourceIds(idsMono), SongDTO.class);
  }
}
