package com.epam.training.microservicefoundation.songservice.service;

import com.epam.training.microservicefoundation.songservice.domain.Song;
import com.epam.training.microservicefoundation.songservice.domain.SongNotFoundException;
import com.epam.training.microservicefoundation.songservice.domain.SongRecord;
import com.epam.training.microservicefoundation.songservice.domain.SongRecordId;
import com.epam.training.microservicefoundation.songservice.repository.SongRepository;
import com.epam.training.microservicefoundation.songservice.service.implementation.IdParameterValidator;
import com.epam.training.microservicefoundation.songservice.service.implementation.SongMapper;
import com.epam.training.microservicefoundation.songservice.service.implementation.SongRecordValidator;
import com.epam.training.microservicefoundation.songservice.service.implementation.SongServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {
    @Mock
    private SongRepository repository;
    @Mock
    private SongMapper mapper;
    @Mock
    private SongRecordValidator songRecordValidator;
    @Mock
    private IdParameterValidator idParameterValidator;
    private SongService service;

    @BeforeEach
    void setup() {
        service = new SongServiceImpl(repository, mapper, songRecordValidator, idParameterValidator);
    }

    @Test
    void shouldSaveSong() {
        Song song = new Song.Builder(1L, "test", "12:00").build();
        when(songRecordValidator.validate(any())).thenReturn(Boolean.TRUE);
        when(mapper.mapToEntity(any())).thenReturn(song);
        when(repository.persist(any())).thenReturn(song);
        SongRecordId songRecordId = service.save(
                new SongRecord.Builder(song.getResourceId(), song.getName(), song.getLength()).build());

        assertNotNull(songRecordId);

        verify(songRecordValidator, times(1)).validate(any());
        verify(mapper, times(1)).mapToEntity(any());
        verify(repository, times(1)).persist(any());
    }


    @Test
    void shouldThrowValidationExceptionWhenSaveSong() {
        when(songRecordValidator.validate(any())).thenReturn(Boolean.FALSE);

        SongRecord songRecord = new SongRecord.Builder(1L,
                "test",
                "12:09").build();

        assertThrows(IllegalArgumentException.class, () -> service.save(songRecord));
    }

    @Test
    void shouldThrowValidationExceptionWhenSaveSongFromDB() {
        when(songRecordValidator.validate(any())).thenReturn(Boolean.TRUE);
        when(repository.persist(any())).thenThrow(DataIntegrityViolationException.class);
        SongRecord songRecord = new SongRecord.Builder(1L, "test", "12:32").build();
        assertThrows(IllegalArgumentException.class, () -> service.save(songRecord));
    }

    @Test
    void shouldGindSongById() {
        Song song = new Song.Builder(1L, "test", "10:00").build();
        when(repository.findById(any())).thenReturn(
                Optional.of(song));
        when(mapper.mapToRecord(any())).thenReturn(new SongRecord.Builder(song.getResourceId(), song.getName(),
                song.getLength()).build());

        SongRecord songRecord = service.getById(1L);
        assertNotNull(songRecord);
        assertEquals(1L, songRecord.getResourceId());
    }

    @Test
    void shouldThrowExceptionWhenGetSongById() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertThrows(SongNotFoundException.class, ()-> service.getById(1L));
    }

    @Test
    void shouldDeleteSongByIds() {
        when(idParameterValidator.validate(any())).thenReturn(Boolean.TRUE);
        doNothing().when(repository).delete(any());
        long[] ids = {1, 2};
        List<SongRecordId> songRecordIds = service.deleteByIds(ids);
        assertNotNull(songRecordIds);
        assertEquals(ids.length, songRecordIds.size());

        verify(idParameterValidator, times(1)).validate(any());
        verify(repository, times(ids.length)).delete(any());
    }

    @Test
    void shouldThrowValidationExceptionWhenDeleteSongByIds() {
        when(idParameterValidator.validate(any())).thenReturn(Boolean.FALSE);
        long[] ids = {1, 2};
        assertThrows(IllegalArgumentException.class, ()-> service.deleteByIds(ids));
    }

}
