package com.epam.training.microservicefoundation.songservice.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epam.training.microservicefoundation.songservice.configuration.TestsMappersConfig;
import com.epam.training.microservicefoundation.songservice.model.dto.GetSongDTO;
import com.epam.training.microservicefoundation.songservice.model.entity.Song;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestsMappersConfig.class)
class GetSongMapperTest {
  @Autowired
  private GetSongMapper getSongMapper;

  private final Song song =
      Song.builder().id(1L).resourceId(123L).name("test").album("mapper").artist("Map").length("12:22").year(2000).build();
  private final GetSongDTO getSongDTO = new GetSongDTO(1L, 123L, "test", "Map", "mapper", "12:22",
      2000);

  @Test
  void toDtoMapping() {
    GetSongDTO dto = getSongMapper.toDto(song);
    assertEquals(getSongDTO, dto);
  }

  @Test
  void toEntityMapping() {
    Song entity = getSongMapper.toEntity(getSongDTO);
    assertEquals(song, entity);
  }
}
