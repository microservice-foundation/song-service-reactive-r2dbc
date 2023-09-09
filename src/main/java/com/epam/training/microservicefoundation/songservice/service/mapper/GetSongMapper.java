package com.epam.training.microservicefoundation.songservice.service.mapper;

import com.epam.training.microservicefoundation.songservice.domain.dto.GetSongDTO;
import com.epam.training.microservicefoundation.songservice.domain.entity.Song;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface GetSongMapper extends BaseMapper<Song, GetSongDTO> {
}
