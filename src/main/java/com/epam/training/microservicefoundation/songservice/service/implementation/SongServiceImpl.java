package com.epam.training.microservicefoundation.songservice.service.implementation;

import com.epam.training.microservicefoundation.songservice.service.mapper.DeleteSongMapper;
import com.epam.training.microservicefoundation.songservice.service.mapper.GetSongMapper;
import com.epam.training.microservicefoundation.songservice.service.mapper.SaveSongMapper;
import com.epam.training.microservicefoundation.songservice.domain.dto.DeleteSongDTO;
import com.epam.training.microservicefoundation.songservice.domain.dto.GetSongDTO;
import com.epam.training.microservicefoundation.songservice.domain.dto.SaveSongDTO;
import com.epam.training.microservicefoundation.songservice.domain.entity.Song;
import com.epam.training.microservicefoundation.songservice.domain.exception.ExceptionSupplier;
import com.epam.training.microservicefoundation.songservice.repository.SongRepository;
import com.epam.training.microservicefoundation.songservice.service.SongService;
import java.util.Arrays;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Logger;
import reactor.util.Loggers;

@Service
@Transactional(readOnly = true)
public class SongServiceImpl implements SongService {
  private static final Logger log = Loggers.getLogger(SongServiceImpl.class);
  private final SongRepository repository;
  private final SaveSongMapper saveSongMapper;
  private final GetSongMapper getSongMapper;
  private final DeleteSongMapper deleteSongMapper;

  public SongServiceImpl(SongRepository repository, SaveSongMapper saveSongMapper, GetSongMapper getSongMapper,
      DeleteSongMapper deleteSongMapper) {
    this.repository = repository;
    this.saveSongMapper = saveSongMapper;
    this.getSongMapper = getSongMapper;
    this.deleteSongMapper = deleteSongMapper;
  }

  @Transactional
  @Override
  public Mono<GetSongDTO> save(Mono<SaveSongDTO> songDTO) {
    log.info("Saving a song.");
    return songDTO
        .map(saveSongMapper::toEntity)
        .flatMap(repository::save)
        .map(getSongMapper::toDto)
        .onErrorMap(DataIntegrityViolationException.class, error -> ExceptionSupplier.entityAlreadyExists(Song.class, error).get());
  }

  @Transactional
  @Override
  public Mono<GetSongDTO> update(final long id, Mono<SaveSongDTO> songDTO) {
    log.info("Updating a song with id={}.", id);
    return songDTO.map(saveSongMapper::toEntity)
        .flatMap(song -> repository.save(song.toBuilder().id(id).build()))
        .map(getSongMapper::toDto)
        .onErrorMap(DataIntegrityViolationException.class, error -> ExceptionSupplier.entityAlreadyExists(Song.class, error).get());
  }

  @Transactional
  @Override
  public Flux<DeleteSongDTO> deleteByIds(final Long[] ids) {
    log.info("Deleting Song(s) with ids '{}'.", Arrays.toString(ids));
    return Flux.fromArray(ids)
        .flatMap(repository::findById)
        .flatMap(this::deleteSong);
  }

  @Override
  public Mono<GetSongDTO> getById(final long id) {
    log.info("Getting a song by id='{}'.", id);
    return repository.findById(id)
        .map(getSongMapper::toDto)
        .switchIfEmpty(Mono.error(ExceptionSupplier.entityNotFound(Song.class, id)));
  }

  @Transactional
  @Override
  public Flux<DeleteSongDTO> deleteByResourceIds(final Long[] ids) {
    log.info("Deleting Song(s) with resource ids '{}'.", Arrays.toString(ids));
    return Flux.fromArray(ids)
        .flatMap(repository::findByResourceId)
        .flatMap(this::deleteSong);
  }

  private Mono<DeleteSongDTO> deleteSong(Song song) {
    return repository.delete(song).thenReturn(deleteSongMapper.toDto(song));
  }
}
