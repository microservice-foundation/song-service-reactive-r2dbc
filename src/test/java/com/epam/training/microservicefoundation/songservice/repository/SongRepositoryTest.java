package com.epam.training.microservicefoundation.songservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epam.training.microservicefoundation.songservice.model.Song;
import java.time.Duration;
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
  @Autowired
  private SongRepository repository;


  @Test
  void shouldSaveSong() {
    Song song = new Song.Builder(6, "We are the champions", "2:59")
        .album("News of the world")
        .year(2001)
        .artist("Queen")
        .build();
    Mono<Song> resultMono = repository.save(song);

    StepVerifier
        .create(resultMono)
        .assertNext(result -> {
          assertEquals(song.getResourceId(), result.getResourceId());
          assertEquals(song.getAlbum(), result.getAlbum());
          assertEquals(song.getArtist(), result.getArtist());
          assertEquals(song.getLength(), result.getLength());
          assertNotNull(result.getCreatedDate());
          assertTrue(result.getId() > 0L);
          assertNotNull(result.getLastModifiedDate());
        })
        .verifyComplete();
  }

  @Test
  void shouldThrowDataIntegrityExceptionWhenSaveSongWithExistentId() {
    Song song = new Song.Builder(1, "Testing well", "10:59")
        .album("Testers")
        .year(2009)
        .artist("Mr.Test")
        .build();

    StepVerifier
        .create(repository.save(song))
        .expectError(DataIntegrityViolationException.class)
        .verify();
  }

  @Test
  void shouldThrowDataIntegrityExceptionWhenSaveSongWithNullForNotNullNameProperty() {
    Song song = new Song.Builder(1, null, "10:59")
        .album("Testers")
        .year(2009)
        .artist("Mr.Test")
        .build();

    StepVerifier
        .create(repository.save(song))
        .expectError(DataIntegrityViolationException.class)
        .verify();
  }

  @Test
  void shouldThrowDataIntegrityExceptionWhenSaveSongWithNullForNotNullLengthProperty() {
    Song song = new Song.Builder(1, "Testing well", null)
        .album("Testers")
        .year(2009)
        .artist("Mr.Test")
        .build();

    StepVerifier
        .create(repository.save(song))
        .expectError(DataIntegrityViolationException.class)
        .verify();
  }

  @Test
  void shouldFindSongById() {
    // create a song
    Song song = new Song.Builder(190L, "Pythoner", "12:34")
        .album("Py snakes")
        .year(2002)
        .artist("Pyer")
        .build();

    StepVerifier
        .create(repository.save(song).flatMap(result -> repository.findById(song.getId())))
        .assertNext(result -> {
          assertEquals(song.getResourceId(), result.getResourceId());
          assertEquals(song.getAlbum(), result.getAlbum());
          assertEquals(song.getArtist(), result.getArtist());
          assertEquals(song.getLength(), result.getLength());
          assertNotNull(result.getCreatedDate());
          assertTrue(result.getId() > 0L);
          assertNotNull(result.getLastModifiedDate());
        })
        .verifyComplete();
  }

  @Test
  void shouldReturnEmptyWhenFindSongById() {
    long id = 123_234_534_456L;

    StepVerifier.create(repository.findById(id))
        .expectSubscription()
        .expectNoEvent(Duration.ofMillis(500))
        .expectComplete();
  }

  @Test
  void shouldDeleteSongById() {
    Song song = new Song.Builder(90L, "Developer", "2:59")
        .album("Hello Worlders")
        .year(2022)
        .artist("Javist")
        .build();

    Mono<Song> resultMono = repository.save(song).flatMap(result -> repository.deleteById(result.getId())
        .flatMap(unused -> repository.findById(result.getId())));

    StepVerifier.create(resultMono)
        .expectSubscription()
        .expectNoEvent(Duration.ofMillis(500))
        .expectComplete();
  }

  @Test
  void shouldThrowEmptyResultDataAccessExceptionWhenDeleteSongById() {
    long id = 123_234_534_456L;
    StepVerifier
        .create(repository.deleteById(id))
        .expectSubscription()
        .expectNoEvent(Duration.ofMillis(500))
        .expectComplete();
  }


  @Test
  void shouldUpdateSong() {
    long id = 1L;
    String name = "Test song for cool guys";
    String album = "test album";
    int year = 1993;

    Mono<Song> songMono = repository.findById(id).flatMap(result -> {
      result.setName(name);
      result.setAlbum(album);
      result.setYear(year);
      return repository.save(result);
    }).flatMap(result -> repository.findById(result.getId()));

    StepVerifier.create(songMono)
        .assertNext(result -> {
          assertEquals(id, result.getId());
          assertEquals(album, result.getAlbum());
          assertEquals(name, result.getName());
          assertEquals(year, result.getYear());
        })
        .verifyComplete();
  }

  @Test
  void shouldThrowExceptionWhenUpdateSongWithNullName() {
    Song song = new Song.Builder(1982L, "Ruby puby", "4:13")
        .album("Hello World in ruby")
        .year(1988)
        .artist("Rubist")
        .build();

    Mono<Song> songMono = repository.save(song).flatMap(result -> {
      result.setName(null);
      return repository.save(result);
    });

    StepVerifier
        .create(songMono)
        .expectError(DataIntegrityViolationException.class)
        .verify();
  }

  @Test
  void shouldThrowExceptionWhenUpdateSongWithNullLength() {
    // create a song
    Song song = new Song.Builder(129L, "Java Developer", "3:59")
        .album("Public world")
        .year(2020)
        .artist("Java man")
        .build();
    Mono<Song> songMono = repository.save(song).flatMap(result -> {
      result.setLength(null);
      return repository.save(result);
    });

    StepVerifier.create(songMono)
        .expectError(DataIntegrityViolationException.class)
        .verify();
  }

  @Test
  void shouldDeleteByResourceId() {
    Song song = new Song.Builder(1818L, "DeleteByResourceId", "18:18").build();
    Mono<Song> songMono = repository.save(song).flatMap(result -> repository.deleteByResourceId(result.getResourceId())
        .flatMap(unused -> repository.findById(result.getId())));

    StepVerifier.create(songMono)
        .expectSubscription()
        .expectNoEvent(Duration.ofMillis(500))
        .expectComplete();
  }
}
