package com.epam.training.microservicefoundation.songservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.training.microservicefoundation.songservice.model.Song;
import com.epam.training.microservicefoundation.songservice.model.SongMetadata;
import com.epam.training.microservicefoundation.songservice.model.SongNotFoundException;
import com.epam.training.microservicefoundation.songservice.model.SongDTO;
import com.epam.training.microservicefoundation.songservice.repository.SongRepository;
import com.epam.training.microservicefoundation.songservice.service.implementation.SongMapper;
import com.epam.training.microservicefoundation.songservice.service.implementation.SongMetadataValidator;
import com.epam.training.microservicefoundation.songservice.service.implementation.SongServiceImpl;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {
  @Mock
  private SongRepository repository;
  @Mock
  private SongMapper mapper;
  @Mock
  private SongMetadataValidator songMetadataValidator;
  private SongService service;

  @BeforeEach
  void setup() {
    service = new SongServiceImpl(repository, mapper, songMetadataValidator);
  }

  @Test
  void shouldSaveSong() {
    Song song = new Song.Builder(1L, "test", "12:00").build();
    when(songMetadataValidator.validate(any())).thenReturn(Boolean.TRUE);
    when(mapper.mapToEntity(any())).thenReturn(song);
    when(repository.save(any())).thenReturn(Mono.just(song));

    Mono<SongDTO> resultMono =
        service.save(Mono.just(new SongMetadata.Builder(song.getResourceId(), song.getName(), song.getLength()).build()));

    StepVerifier.create(resultMono)
        .assertNext(result -> {
          assertNotNull(result);
          assertEquals(song.getId(), result.getId());
          assertEquals(song.getResourceId(), result.getResourceId());
        })
        .verifyComplete();

    verify(songMetadataValidator, times(1)).validate(any());
    verify(mapper, times(1)).mapToEntity(any());
    verify(repository, times(1)).save(any());
  }

  @Test
  void shouldThrowValidationExceptionWhenSaveSong() {
    when(songMetadataValidator.validate(any())).thenReturn(Boolean.FALSE);

    SongMetadata songMetadata = new SongMetadata.Builder(1L, "test", "12:09").build();

    StepVerifier.create(service.save(Mono.just(songMetadata)))
        .expectError(IllegalArgumentException.class)
        .verify();
  }

  @Test
  void shouldThrowValidationExceptionWhenSaveSongFromDB() {
    when(songMetadataValidator.validate(any())).thenReturn(Boolean.TRUE);
    when(repository.save(any())).thenReturn(Mono.error(new DataIntegrityViolationException("Invalid field")));
    SongMetadata songMetadata = new SongMetadata.Builder(1L, "test", "12:32").build();

    StepVerifier.create(service.save(Mono.just(songMetadata)))
        .expectError(IllegalArgumentException.class)
        .verify();
  }

  @Test
  void shouldGetSongById() {
    Song song = new Song.Builder(1L, "test", "10:00").build();
    when(repository.findById(anyLong())).thenReturn(Mono.just(song));
    when(mapper.mapToRecord(any())).thenReturn(new SongMetadata.Builder(song.getResourceId(), song.getName(), song.getLength()).build());

    StepVerifier.create(service.getById(1L))
        .assertNext(result -> {
          assertNotNull(result);
          assertEquals(song.getId(), result.getId());
          assertEquals(song.getResourceId(), result.getResourceId());
          assertEquals(song.getName(), result.getName());
          assertEquals(song.getLength(), result.getLength());
        })
      .verifyComplete();
  }

  @Test
  void shouldThrowExceptionWhenGetSongById() {
    when(repository.findById(anyLong())).thenReturn(Mono.empty());
    StepVerifier.create(service.getById(1L))
        .expectError(SongNotFoundException.class)
        .verify();
  }

  @Test
  void shouldDeleteSongByIds() {
    when(repository.delete(any())).thenReturn(Mono.empty());
    List<Long> ids = List.of(1L, 2L);
    when(repository.findById(ids.get(0)))
        .thenReturn(Mono.just(new Song.Builder(199L, "test1", "12:33").id(ids.get(0)).build()));
    when(repository.findById(ids.get(1)))
        .thenReturn(Mono.just(new Song.Builder(200L, "test2", "12:32").id(ids.get(1)).build()));

    StepVerifier.create(service.deleteByIds(Flux.fromIterable(ids)))
        .assertNext(result -> {
          assertNotNull(result);
          assertEquals(ids.get(0), result.getId());
        })
        .assertNext(result -> {
          assertNotNull(result);
          assertEquals(ids.get(1), result.getId());
        })
        .verifyComplete();

    verify(repository, times(ids.size())).findById(anyLong());
    verify(repository, times(ids.size())).delete(any());
  }

  @Test
  void shouldThrowValidationExceptionWhenDeleteSongByIds() {
    List<Long> ids = Collections.emptyList();
    StepVerifier.create(service.deleteByIds(Flux.fromIterable(ids)))
        .expectError(IllegalArgumentException.class)
        .verify();
  }

  @Test
  void shouldDeleteByResourceIds() {
    when(repository.delete(any())).thenReturn(Mono.empty());
    List<Long> ids = List.of(199L, 200L);
    when(repository.findByResourceId(ids.get(0))).thenReturn(Mono.just(new Song.Builder(
        199L, "test1", "12:33").id(ids.get(0)).build()));
    when(repository.findByResourceId(ids.get(1))).thenReturn(Mono.just(new Song.Builder(
        200L, "test2", "12:32").id(ids.get(1)).build()));

    StepVerifier.create(service.deleteByResourceIds(Flux.fromIterable(ids)))
        .assertNext(result -> {
          assertNotNull(result);
          assertEquals(ids.get(0), result.getId());
        })
        .assertNext(result -> {
          assertNotNull(result);
          assertEquals(ids.get(1), result.getId());
        })
        .verifyComplete();

    verify(repository, times(ids.size())).findByResourceId(anyLong());
    verify(repository, times(ids.size())).delete(any());
  }

  @Test
  void shouldThrowValidationExceptionWhenDeleteSongByResourceIds() {
    List<Long> ids = Collections.emptyList();
    StepVerifier.create(service.deleteByResourceIds(Flux.fromIterable(ids)))
        .expectError(IllegalArgumentException.class)
        .verify();
  }
}
