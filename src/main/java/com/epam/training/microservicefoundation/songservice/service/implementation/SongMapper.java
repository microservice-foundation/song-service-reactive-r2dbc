package com.epam.training.microservicefoundation.songservice.service.implementation;

import com.epam.training.microservicefoundation.songservice.domain.Song;
import com.epam.training.microservicefoundation.songservice.domain.SongRecord;
import com.epam.training.microservicefoundation.songservice.service.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SongMapper implements Mapper<Song, SongRecord> {
    private static final Logger log = LoggerFactory.getLogger(SongMapper.class);
    @Override
    public SongRecord mapToRecord(Song song) {
        log.info("Mapping entity '{}' to record", song);
        if(song == null) {
            return null;
        }
        return new SongRecord.Builder(song.getResourceId(), song.getName(), song.getLength())
                .album(song.getAlbum())
                .artist(song.getArtist())
                .year(song.getYear()).build();
    }

    @Override
    public Song mapToEntity(SongRecord songRecord) {
        log.info("Mapping record '{}' to entity", songRecord);
        if(songRecord == null) {
            return null;
        }
        return new Song.Builder(songRecord.getResourceId(), songRecord.getName(), songRecord.getLength())
                .artist(songRecord.getArtist())
                .album(songRecord.getAlbum())
                .year(songRecord.getYear())
                .build();
    }
}
