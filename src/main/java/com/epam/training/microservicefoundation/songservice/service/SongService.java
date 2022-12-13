package com.epam.training.microservicefoundation.songservice.service;

import com.epam.training.microservicefoundation.songservice.domain.SongRecord;
import com.epam.training.microservicefoundation.songservice.domain.SongRecordId;

import java.util.List;

public interface SongService {
    SongRecordId save(SongRecord songRecord);
    SongRecord update(SongRecord songRecord);
    List<SongRecordId> deleteByIds(long[] ids);
    SongRecord getById(long id);
    List<SongRecordId> deleteByResourceIds(long[] ids);
}
