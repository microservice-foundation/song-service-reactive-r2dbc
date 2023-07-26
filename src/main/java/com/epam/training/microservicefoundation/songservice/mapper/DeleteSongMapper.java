package com.epam.training.microservicefoundation.songservice.mapper;

import com.epam.training.microservicefoundation.songservice.model.dto.DeleteSongDTO;
import com.epam.training.microservicefoundation.songservice.model.entity.Song;
import org.mapstruct.Mapper;

@Mapper(config = BaseMapper.class)
public interface DeleteSongMapper extends BaseMapper<Song, DeleteSongDTO> {
}
