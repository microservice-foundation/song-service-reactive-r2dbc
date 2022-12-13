package com.epam.training.microservicefoundation.songservice.repository;

import com.epam.training.microservicefoundation.songservice.domain.Song;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends BaseJpaRepository<Song, Long>, BaseRepository<Song> {
    void deleteByResourceId(long resourceId);
}
