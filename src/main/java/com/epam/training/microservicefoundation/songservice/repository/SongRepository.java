package com.epam.training.microservicefoundation.songservice.repository;

import com.epam.training.microservicefoundation.songservice.model.entity.Song;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SongRepository extends ReactiveCrudRepository<Song, Long> {
  Mono<Void> deleteByResourceId(long resourceId);
  Mono<Song> findByResourceId(long resourceId);
}
