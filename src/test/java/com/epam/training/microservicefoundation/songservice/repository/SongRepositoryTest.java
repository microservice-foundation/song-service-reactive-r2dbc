package com.epam.training.microservicefoundation.songservice.repository;

import com.epam.training.microservicefoundation.songservice.domain.Song;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ExtendWith(PostgresExtension.class)
@DirtiesContext
@Sql(value = "/sql/data.sql")
@TestPropertySource(locations = "classpath:application.yaml")
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
        Song result = repository.persistAndFlush(song);
        assertNotNull(result);
        assertTrue(song.getId() > 0L);
        assertEquals(song.getResourceId(), result.getResourceId());
        assertEquals(song.getName(), result.getName());
        assertEquals(song.getLength(), result.getLength());
        assertNotNull(song.getCreatedDate());
        assertNotNull(song.getLastModifiedDate());
    }

    @Test
    void shouldThrowDataIntegrityExceptionWhenSaveSongWithExistentId() {
        Song song = new Song.Builder(1, "Testing well", "10:59")
                .album("Testers")
                .year(2009)
                .artist("Mr.Test")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> repository.persistAndFlush(song));
    }

    @Test
    void shouldThrowDataIntegrityExceptionWhenSaveSongWithNullForNotNullNameProperty() {
        Song song = new Song.Builder(1, null, "10:59")
                .album("Testers")
                .year(2009)
                .artist("Mr.Test")
                .build();


        assertThrows(DataIntegrityViolationException.class, () -> repository.persistAndFlush(song));
    }

    @Test
    void shouldThrowDataIntegrityExceptionWhenSaveSongWithNullForNotNullLengthProperty() {
        Song song = new Song.Builder(1, "Testing well", null)
                .album("Testers")
                .year(2009)
                .artist("Mr.Test")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> repository.persistAndFlush(song));
    }

    @Test
    void shouldFindSongById() {
        // create a song
        Song song = new Song.Builder(190L, "Pythoner", "12:34")
                .album("Py snakes")
                .year(2002)
                .artist("Pyer")
                .build();
        Song result = repository.persistAndFlush(song);

        Optional<Song> songOptional = repository.findById(result.getId());
        assertTrue(songOptional.isPresent());
        assertEquals(result.getId(), songOptional.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenFindSongById() {
        long id = 123_234_534_456L;
        Optional<Song> result = repository.findById(id);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteSongById() {
        Song song = new Song.Builder(90L, "Developer", "2:59")
                .album("Hello Worlders")
                .year(2022)
                .artist("Javist")
                .build();
        Song result = repository.persistAndFlush(song);

        repository.deleteById(result.getId());

        boolean isExistent = repository.existsById(result.getId());
        assertFalse(isExistent);
    }

    @Test
    void shouldThrowEmptyResultDataAccessExceptionWhenDeleteSongById() {
        long id = 123_234_534_456L;
        assertThrows(EmptyResultDataAccessException.class, ()-> repository.deleteById(id));
    }

    @Test
    void shouldDeleteSong() {
        Song song = new Song.Builder(11L, "Js is good", "3:14")
                .album("Hello World in js")
                .year(1998)
                .artist("Jsist")
                .build();
        Song result = repository.persistAndFlush(song);

        repository.delete(result);

        boolean isExistent = repository.existsById(result.getId());
        assertFalse(isExistent);
    }

    @Test
    void shouldUpdateSong() {
        Optional<Song> preUpdate = repository.findById(1L);
        assertTrue(preUpdate.isPresent());
        Song song = preUpdate.get();
        song.setYear(1998);
        repository.updateAndFlush(song);

        Optional<Song> postUpdate = repository.findById(1L);
        assertTrue(postUpdate.isPresent());

        assertEquals(song.getResourceId(), postUpdate.get().getResourceId());
        assertEquals(song.getName(), postUpdate.get().getName());
        assertEquals(song.getLength(), postUpdate.get().getLength());
        assertEquals(song.getYear(), postUpdate.get().getYear());
    }

    @Test
    void shouldThrowExceptionWhenUpdateSongWithNullName() {
        Song song = new Song.Builder(1982L, "Ruby puby", "4:13")
                .album("Hello World in ruby")
                .year(1988)
                .artist("Rubist")
                .build();
        Song result = repository.persistAndFlush(song);

        result.setName(null);
        assertThrows(DataIntegrityViolationException.class, ()-> repository.updateAndFlush(result));
    }

    @Test
    void shouldThrowExceptionWhenUpdateSongWithNullLength() {
        // create a song
        Song song = new Song.Builder(129L, "Java Developer", "3:59")
                .album("Public world")
                .year(2020)
                .artist("Java man")
                .build();
        repository.persistAndFlush(song);

        song.setLength(null);
        assertThrows(DataIntegrityViolationException.class, ()-> repository.updateAndFlush(song));
    }

    @Test
    void shouldDeleteByResourceId() {
        Song song = new Song.Builder(1818L, "DeleteByResourceId", "18:18").build();
        repository.persist(song);

        repository.deleteByResourceId(song.getResourceId());

        Optional<Song> songOptional1 = repository.findById(song.getId());
        assertTrue(songOptional1.isEmpty());
    }
}
