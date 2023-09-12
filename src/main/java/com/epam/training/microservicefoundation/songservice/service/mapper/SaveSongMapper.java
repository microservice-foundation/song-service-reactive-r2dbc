package com.epam.training.microservicefoundation.songservice.service.mapper;

import com.epam.training.microservicefoundation.songservice.domain.dto.SaveSongDTO;
import com.epam.training.microservicefoundation.songservice.domain.entity.Song;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface SaveSongMapper extends BaseMapper<Song, SaveSongDTO> {
}
