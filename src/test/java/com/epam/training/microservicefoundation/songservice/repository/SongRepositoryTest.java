package com.epam.training.microservicefoundation.songservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epam.training.microservicefoundation.songservice.configuration.DatasourceConfiguration;
import com.epam.training.microservicefoundation.songservice.model.entity.Song;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataR2dbcTest
@ExtendWith(PostgresExtension.class)
@DirtiesContext
@ContextConfiguration(classes = DatasourceConfiguration.class)
@TestPropertySource(locations = "classpath:application.properties")
class SongRepositoryTest {
  private final Song SONG = Song.builder().resourceId(6).name("Champions").length("2:13").album("News of the world").year(2001)
      .artist("Queen").build();
  @Autowired
  private SongRepository repository;

  @AfterEach
  public void cleanUp() {
    StepVerifier.create(repository.deleteAll())
        .verifyComplete();
  }

  @Test
  void shouldSaveSong() {
    assertSongResult(SONG, repository.save(SONG));
  }

  @Test
  void shouldThrowDataIntegrityExceptionWhenSaveSongWithExistentId() {
    assertSongResult(SONG, repository.save(SONG));

    StepVerifier
        .create(repository.save(SONG.toBuilder().id(0).build()))
        .expectError(DataIntegrityViolationException.class)
        .verify();
  }

  @Test
  void shouldThrowDataIntegrityExceptionWhenSaveSongWithNullForNotNullNameProperty() {
    StepVerifier
        .create(repository.save(SONG.toBuilder().name(null).build()))
        .expectError(DataIntegrityViolationException.class)
        .verify();
  }

  @Test
  void shouldThrowDataIntegrityExceptionWhenSaveSongWithNullForNotNullLengthProperty() {
    StepVerifier
        .create(repository.save(SONG.toBuilder().length(null).build()))
        .expectError(DataIntegrityViolationException.class)
        .verify();
  }

  @Test
  void shouldFindSongById() {
    assertSongResult(SONG, repository.save(SONG));
    StepVerifier.create(repository.findById(SONG.getId()))
        .assertNext(result -> {
          assertEquals(SONG.getId(), result.getId());
          assertEquals(SONG.getResourceId(), result.getResourceId());
          assertEquals(SONG.getAlbum(), result.getAlbum());
          assertEquals(SONG.getArtist(), result.getArtist());
          assertEquals(SONG.getLength(), result.getLength());
          assertNotNull(result.getCreatedDate());
          assertNotNull(result.getLastModifiedDate());
        }).verifyComplete();
  }

  @Test
  void shouldReturnEmptyWhenFindSongById() {
    long id = 123_234_534_456L;

    StepVerifier.create(repository.findById(id))
        .expectSubscription()
        .expectNextCount(0)
        .expectComplete();
  }

  @Test
  void shouldDeleteSongById() {
    assertSongResult(SONG, repository.save(SONG));

    final long songId = SONG.getId();
    StepVerifier.create(repository.deleteById(songId))
        .expectSubscription()
        .expectNextCount(0)
        .verifyComplete();

    StepVerifier.create(repository.findById(songId))
        .expectSubscription()
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  void shouldThrowEmptyResultDataAccessExceptionWhenDeleteSongById() {
    long id = 123_234_534_456L;
    StepVerifier
        .create(repository.deleteById(id))
        .expectSubscription()
        .expectNextCount(0)
        .expectComplete();
  }

  @Test
  void shouldUpdateSong() {
    assertSongResult(SONG, repository.save(SONG));
    Song updatedSong =
        SONG.toBuilder().resourceId(998L).name("Cup of tea").album("Office").artist("Receptionist").length("21:33").year(2022).build();

    StepVerifier.create(repository.save(updatedSong))
        .assertNext(result -> {
          assertEquals(SONG.getId(), result.getId());
          assertEquals(updatedSong.getName(), result.getName());
          assertEquals(updatedSong.getLength(), result.getLength());
        })
        .verifyComplete();
  }

  @Test
  void shouldThrowExceptionWhenUpdateSongWithNullName() {
    assertSongResult(SONG, repository.save(SONG));

    StepVerifier
        .create(repository.save(SONG.toBuilder().name(null).build()))
        .expectError(DataIntegrityViolationException.class)
        .verify();
  }

  @Test
  void shouldThrowExceptionWhenUpdateSongWithNullLength() {
    // create a song
    assertSongResult(SONG, repository.save(SONG));

    StepVerifier.create(repository.save(SONG.toBuilder().length(null).build()))
        .expectError(DataIntegrityViolationException.class)
        .verify();
  }

  @Test
  void shouldDeleteByResourceId() {
    assertSongResult(SONG, repository.save(SONG));
    final long songId = SONG.getId();
    StepVerifier.create(repository.findById(songId))
        .expectSubscription().expectNextCount(1).verifyComplete();

    StepVerifier.create(repository.deleteById(songId))
            .expectSubscription().expectNextCount(0).verifyComplete();

    StepVerifier.create(repository.findById(songId))
        .expectSubscription().expectNextCount(0).verifyComplete();
  }

  @Test
  void shouldThrowEmptyResultDataAccessExceptionWhenDeleteSongByResourceId() {
    long id = 123_234_534_456L;
    StepVerifier
        .create(repository.deleteByResourceId(id))
        .expectSubscription()
        .expectNextCount(0)
        .expectComplete();
  }


  private void assertSongResult(Song expected, Mono<Song> actual) {
    StepVerifier
        .create(actual)
        .assertNext(result -> {
          assertEquals(expected.getResourceId(), result.getResourceId());
          assertEquals(expected.getAlbum(), result.getAlbum());
          assertEquals(expected.getArtist(), result.getArtist());
          assertEquals(expected.getLength(), result.getLength());
          assertNotNull(result.getCreatedDate());
          assertTrue(result.getId() > 0L);
          assertNotNull(result.getLastModifiedDate());
        })
        .verifyComplete();
  }
}
