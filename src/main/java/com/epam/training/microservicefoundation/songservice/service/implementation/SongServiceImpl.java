package com.epam.training.microservicefoundation.songservice.service.implementation;

import com.epam.training.microservicefoundation.songservice.model.Song;
import com.epam.training.microservicefoundation.songservice.model.SongDTO;
import com.epam.training.microservicefoundation.songservice.model.SongMetadata;
import com.epam.training.microservicefoundation.songservice.model.SongNotFoundException;
import com.epam.training.microservicefoundation.songservice.repository.SongRepository;
import com.epam.training.microservicefoundation.songservice.service.Mapper;
import com.epam.training.microservicefoundation.songservice.service.SongService;
import com.epam.training.microservicefoundation.songservice.service.Validator;
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
  private final Mapper<Song, SongMetadata> songMapper;
  private final Validator<SongMetadata> songRecordValidator;

  public SongServiceImpl(SongRepository repository, Mapper<Song, SongMetadata> songMapper, Validator<SongMetadata> songRecordValidator) {
    this.repository = repository;
    this.songMapper = songMapper;
    this.songRecordValidator = songRecordValidator;
  }


  @Transactional
  @Override
  public Mono<SongDTO> save(Mono<SongMetadata> songMetadata) {
    log.info("Saving a song record '{}'", songMetadata);

    return songMetadata
        .filter(songRecordValidator::validate)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Saving invalid song record")))
        .flatMap(metadata -> repository.save(songMapper.mapToEntity(metadata))
            .onErrorResume(error -> Mono.error(new IllegalArgumentException(String.format(
                "Saving a song record with invalid parameters length or duplicate value '%s'", error.getLocalizedMessage()), error)))
            .map(song -> new SongDTO(song.getId(), song.getResourceId())));

  }

  @Transactional
  @Override
  public Mono<SongMetadata> update(Mono<SongMetadata> songMetadata) {
    log.info("Updating a song record '{}'", songMetadata);

    return songMetadata
        .filter(songRecordValidator::validate)
        .flatMap(metadata -> repository.save(songMapper.mapToEntity(metadata))
            .onErrorResume(error -> Mono.error(new IllegalArgumentException(String.format(
                "Updating a song record with invalid parameters length or duplicate value '%s'", error.getLocalizedMessage()), error)))
            .map(songMapper::mapToRecord))
        .switchIfEmpty(Mono.error(new IllegalArgumentException(String.format("Updating an invalid song record '%s'", songMetadata))));

  }

  @Transactional
  @Override
  public Flux<SongDTO> deleteByIds(Flux<Long> ids) {
    log.info("Deleting Song(s) with id {}", ids);
    return ids
        .flatMap(repository::findById)
        .flatMap(song -> repository.delete(song).thenReturn(new SongDTO(song.getId(), song.getResourceId())))
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Id param is not validated, check your ids")));
  }

  @Override
  public Mono<SongMetadata> getById(long id) {
    log.info("Getting a song with id '{}'", id);
    return repository.findById(id)
        .map(songMapper::mapToRecord)
        .switchIfEmpty(Mono.error(new SongNotFoundException(String.format("Song is not found with id '%d'", id))));
  }

  @Transactional
  @Override
  public Flux<SongDTO> deleteByResourceIds(Flux<Long> ids) {
    log.info("Deleting Song(s) with resource id(s) '{}'", ids);
    return ids
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Id param is not validated, check your ids")))
        .flatMap(repository::findByResourceId)
        .flatMap(song -> repository.delete(song).thenReturn(new SongDTO(song.getId(), song.getResourceId())))
        .log();
  }
}
