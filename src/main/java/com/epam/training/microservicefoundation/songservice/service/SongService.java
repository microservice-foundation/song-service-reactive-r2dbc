package com.epam.training.microservicefoundation.songservice.service;

import com.epam.training.microservicefoundation.songservice.model.SongDTO;
import com.epam.training.microservicefoundation.songservice.model.SongMetadata;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SongService {
  Mono<SongDTO> save(Mono<SongMetadata> songMetadata);

  Mono<SongMetadata> update(Mono<SongMetadata> songMetadata);

  Flux<SongDTO> deleteByIds(Flux<Long> ids);

  Mono<SongMetadata> getById(long id);

  Flux<SongDTO> deleteByResourceIds(Flux<Long> ids);
}
