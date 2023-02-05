package com.epam.training.microservicefoundation.songservice.service;

import com.epam.training.microservicefoundation.songservice.model.SongMetadata;
import com.epam.training.microservicefoundation.songservice.model.SongRecord;

import java.util.List;

public interface SongService {
    SongRecord save(SongMetadata songMetadata);
    SongMetadata update(SongMetadata songMetadata);
    List<SongRecord> deleteByIds(long[] ids);
    SongMetadata getById(long id);
    List<SongRecord> deleteByResourceIds(long[] ids);
}
