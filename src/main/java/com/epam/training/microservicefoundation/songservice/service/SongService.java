package com.epam.training.microservicefoundation.songservice.service;

import com.epam.training.microservicefoundation.songservice.model.SongMetadata;
import com.epam.training.microservicefoundation.songservice.model.SongRecord;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SongService {
  Mono<SongRecord> save(Mono<SongMetadata> songMetadata);

  Mono<SongMetadata> update(Mono<SongMetadata> songMetadata);

  Flux<SongRecord> deleteByIds(Flux<Long> ids);

  Mono<SongMetadata> getById(long id);

  Flux<SongRecord> deleteByResourceIds(Flux<Long> ids);
}
