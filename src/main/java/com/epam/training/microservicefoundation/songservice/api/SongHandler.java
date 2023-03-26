package com.epam.training.microservicefoundation.songservice.api;

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
    return service.save(request.bodyToMono(SongMetadata.class))
        .flatMap(songRecord -> ServerResponse.created(URI.create(request.path() + "/" + songRecord.getId()))
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(songRecord));
  }

  public Mono<ServerResponse> update(ServerRequest request) {
    return service.update(request.bodyToMono(SongMetadata.class))
        .flatMap(songMetadata -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(songMetadata));
  }

  public Mono<ServerResponse> deleteByIds(ServerRequest request) {
    Flux<Long> idsFlux = request
        .queryParam("id")
        .map(string -> Flux.fromArray(string.split(",")).map(Long::parseLong))
        .orElse(Flux.empty());

    return service.deleteByIds(idsFlux)
        .collectList()
        .flatMap(songRecords -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(songRecords));
  }

  public Mono<ServerResponse> getById(ServerRequest request) {
    long id = Long.parseLong(request.pathVariable("id"));
    return service.getById(id)
        .flatMap(songMetadata -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(songMetadata));
  }

  public Mono<ServerResponse> deleteByResourceIds(ServerRequest request) {
    Flux<Long> idsMono = request
        .queryParam("id")
        .map(string -> Flux.fromArray(string.split(",")).map(Long::parseLong))
        .orElse(Flux.empty());

    return service.deleteByResourceIds(idsMono)
        .collectList()
        .flatMap(songRecords -> ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(songRecords));
  }
}
