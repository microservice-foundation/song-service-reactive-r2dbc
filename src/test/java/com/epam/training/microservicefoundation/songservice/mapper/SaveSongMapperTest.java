package com.epam.training.microservicefoundation.songservice.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epam.training.microservicefoundation.songservice.configuration.TestsMappersConfig;
import com.epam.training.microservicefoundation.songservice.model.dto.SaveSongDTO;
import com.epam.training.microservicefoundation.songservice.model.entity.Song;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestsMappersConfig.class)
class SaveSongMapperTest {

  @Autowired
  private SaveSongMapper saveSongMapper;
  private final Song song =
      Song.builder().resourceId(123L).name("test").album("mapper").artist("Map").lengthInSeconds(12).year(2000).build();
  private final SaveSongDTO saveSongDTO = new SaveSongDTO(123L, "test", "Map", "mapper", 12,
      2000);

  @Test
  void toEntityMapping() {
    Song entity = saveSongMapper.toEntity(saveSongDTO);
    assertEquals(song, entity);
  }

  @Test
  void toDtoMapping() {
    SaveSongDTO dto = saveSongMapper.toDto(song);
    assertEquals(saveSongDTO, dto);
  }
}
