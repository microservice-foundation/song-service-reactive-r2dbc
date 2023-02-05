package com.epam.training.microservicefoundation.songservice.service.implementation;

import com.epam.training.microservicefoundation.songservice.model.Song;
import com.epam.training.microservicefoundation.songservice.model.SongMetadata;
import com.epam.training.microservicefoundation.songservice.service.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SongMapper implements Mapper<Song, SongMetadata> {
    private static final Logger log = LoggerFactory.getLogger(SongMapper.class);
    @Override
    public SongMetadata mapToRecord(Song song) {
        log.info("Mapping entity '{}' to record", song);
        if(song == null) {
            return null;
        }
        return new SongMetadata.Builder(song.getResourceId(), song.getName(), song.getLength())
                .album(song.getAlbum())
                .artist(song.getArtist())
                .year(song.getYear()).build();
    }

    @Override
    public Song mapToEntity(SongMetadata songMetadata) {
        log.info("Mapping record '{}' to entity", songMetadata);
        if(songMetadata == null) {
            return null;
        }
        return new Song.Builder(songMetadata.getResourceId(), songMetadata.getName(), songMetadata.getLength())
                .artist(songMetadata.getArtist())
                .album(songMetadata.getAlbum())
                .year(songMetadata.getYear())
                .build();
    }
}
