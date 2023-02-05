package com.epam.training.microservicefoundation.songservice.repository;

import com.epam.training.microservicefoundation.songservice.model.Song;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends BaseJpaRepository<Song, Long>, BaseRepository<Song> {
    void deleteByResourceId(long resourceId);
    Optional<Song> findByResourceId(long resourceId);
}
