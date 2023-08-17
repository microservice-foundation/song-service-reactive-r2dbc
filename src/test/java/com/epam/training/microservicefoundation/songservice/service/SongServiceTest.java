package com.epam.training.microservicefoundation.songservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.training.microservicefoundation.songservice.mapper.DeleteSongMapper;
import com.epam.training.microservicefoundation.songservice.mapper.GetSongMapper;
import com.epam.training.microservicefoundation.songservice.mapper.SaveSongMapper;
import com.epam.training.microservicefoundation.songservice.model.dto.DeleteSongDTO;
import com.epam.training.microservicefoundation.songservice.model.dto.GetSongDTO;
import com.epam.training.microservicefoundation.songservice.model.dto.SaveSongDTO;
import com.epam.training.microservicefoundation.songservice.model.entity.Song;
import com.epam.training.microservicefoundation.songservice.model.exception.EntityExistsException;
import com.epam.training.microservicefoundation.songservice.model.exception.EntityNotFoundException;
import com.epam.training.microservicefoundation.songservice.repository.SongRepository;
import com.epam.training.microservicefoundation.songservice.service.implementation.SongServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {
  @Mock
  private SongRepository repository;
  @Mock
  private SaveSongMapper saveSongMapper;
  @Mock
  private DeleteSongMapper deleteSongMapper;
  @Mock
  private GetSongMapper getSongMapper;
  @InjectMocks
  private SongServiceImpl service;
  private final Song songEntity =
      Song.builder().resourceId(123L).name("Alone").album("Solo").artist("Justin Timberland").length("4:58").year(1998).build();
  private final Song savedSongEntity1 =
      Song.builder().id(1L).resourceId(123L).name("Alone").album("Solo").artist("Justin Timberland").length("4:58").year(1998).build();
  private final Song savedSongEntity2 =
      Song.builder().id(2L).resourceId(124L).name("Couple").album("Due").artist("Justin Timberland").length("3:58").year(1990).build();
  private final SaveSongDTO saveSongDTO = new SaveSongDTO(123L, "Alone", "Justin Timberland", "Solo", "4:58", 1998);
  private final GetSongDTO getSongDTO = new GetSongDTO(1L, 123L, "Alone", "Justin Timberland", "Solo", "4:58", 1998);
  private final DeleteSongDTO deleteSongDTO1 = new DeleteSongDTO(1L, 123L);
  private final DeleteSongDTO deleteSongDTO2 = new DeleteSongDTO(2L, 124L);

  @Test
  void shouldSaveSong() {
    when(saveSongMapper.toEntity(any())).thenReturn(songEntity);
    when(repository.save(any())).thenReturn(Mono.just(savedSongEntity1));
    when(getSongMapper.toDto(any())).thenReturn(getSongDTO);

    StepVerifier.create(service.save(Mono.just(saveSongDTO)))
        .assertNext(result -> {
          assertNotNull(result);
          assertEquals(savedSongEntity1.getId(), result.getId());
          assertEquals(savedSongEntity1.getResourceId(), result.getResourceId());
        })
        .verifyComplete();

    verify(saveSongMapper, times(1)).toEntity(any());
    verify(repository, times(1)).save(any());
    verify(getSongMapper, times(1)).toDto(any());
  }

  @Test
  void shouldThrowValidationExceptionWhenSaveSongFromDB() {
    when(saveSongMapper.toEntity(any())).thenReturn(songEntity);
    when(repository.save(any())).thenReturn(Mono.error(new DataIntegrityViolationException("Invalid field")));

    StepVerifier.create(service.save(Mono.just(saveSongDTO)))
        .expectError(EntityExistsException.class)
        .verify();
  }

  @Test
  void shouldGetSongById() {
    when(repository.findById(anyLong())).thenReturn(Mono.just(savedSongEntity1));
    when(getSongMapper.toDto(any())).thenReturn(getSongDTO);

    StepVerifier.create(service.getById(1L))
        .assertNext(result -> {
          assertNotNull(result);
          assertEquals(savedSongEntity1.getId(), result.getId());
          assertEquals(savedSongEntity1.getResourceId(), result.getResourceId());
          assertEquals(savedSongEntity1.getName(), result.getName());
          assertEquals(savedSongEntity1.getLength(), result.getLength());
        })
        .verifyComplete();
  }

  @Test
  void shouldThrowExceptionWhenGetSongById() {
    when(repository.findById(anyLong())).thenReturn(Mono.empty());
    StepVerifier.create(service.getById(1L))
        .expectError(EntityNotFoundException.class)
        .verify();
  }

  @Test
  void shouldDeleteSongByIds() {
    Long[] ids = {savedSongEntity1.getId(), savedSongEntity2.getId()};
    when(repository.findById(ids[0])).thenReturn(Mono.just(savedSongEntity1));
    when(repository.findById(ids[1])).thenReturn(Mono.just(savedSongEntity2));
    when(repository.delete(any())).thenReturn(Mono.empty());
    when(deleteSongMapper.toDto(savedSongEntity1)).thenReturn(new DeleteSongDTO(savedSongEntity1.getId(),
        savedSongEntity1.getResourceId()));
    when(deleteSongMapper.toDto(savedSongEntity2)).thenReturn(new DeleteSongDTO(savedSongEntity2.getId(),
        savedSongEntity2.getResourceId()));

    StepVerifier.create(service.deleteByIds(ids))
        .assertNext(result -> {
          assertNotNull(result);
          assertEquals(savedSongEntity1.getId(), result.getId());
          assertEquals(savedSongEntity1.getResourceId(), result.getResourceId());
        })
        .assertNext(result -> {
          assertNotNull(result);
          assertEquals(savedSongEntity2.getId(), result.getId());
          assertEquals(savedSongEntity2.getResourceId(), result.getResourceId());
        })
        .verifyComplete();
  }

  @Test
  void shouldPassNormallyWhenDeleteSongByIds() {
    StepVerifier.create(service.deleteByIds(new Long[0]))
        .expectSubscription()
        .expectNextCount(0L)
        .verifyComplete();
  }

  @Test
  void shouldDeleteByResourceIds() {
    Long[] resourceIds = {savedSongEntity1.getResourceId(), savedSongEntity2.getResourceId()};
    when(repository.findByResourceId(resourceIds[0])).thenReturn(Mono.just(savedSongEntity1));
    when(repository.findByResourceId(resourceIds[1])).thenReturn(Mono.just(savedSongEntity2));
    when(repository.delete(any())).thenReturn(Mono.empty());
    when(deleteSongMapper.toDto(savedSongEntity1)).thenReturn(deleteSongDTO1);

    when(deleteSongMapper.toDto(savedSongEntity2)).thenReturn(deleteSongDTO2);

    StepVerifier.create(service.deleteByResourceIds(resourceIds))
        .assertNext(result -> {
          assertNotNull(result);
          assertEquals(savedSongEntity1.getId(), result.getId());
          assertEquals(savedSongEntity1.getResourceId(), result.getResourceId());
        })
        .assertNext(result -> {
          assertNotNull(result);
          assertEquals(savedSongEntity2.getId(), result.getId());
          assertEquals(savedSongEntity2.getResourceId(), result.getResourceId());
        })
        .verifyComplete();
  }

  @Test
  void shouldPassNormallyWhenDeleteSongByResourceIds() {
    StepVerifier.create(service.deleteByResourceIds(new Long[0]))
        .expectSubscription()
        .expectNextCount(0)
        .verifyComplete();
  }
}
