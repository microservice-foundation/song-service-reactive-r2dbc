package com.epam.training.microservicefoundation.songservice.service;

import com.epam.training.microservicefoundation.songservice.model.dto.DeleteSongDTO;
import com.epam.training.microservicefoundation.songservice.model.dto.GetSongDTO;
import com.epam.training.microservicefoundation.songservice.model.dto.SaveSongDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SongService {
  Mono<GetSongDTO> save(Mono<SaveSongDTO> songDTO);
  Mono<GetSongDTO> update(final long id, Mono<SaveSongDTO> songDTO);
  Flux<DeleteSongDTO> deleteByIds(final Long[] ids);
  Mono<GetSongDTO> getById(final long id);
  Flux<DeleteSongDTO> deleteByResourceIds(final Long[] ids);
}
