package com.epam.training.microservicefoundation.songservice.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.epam.training.microservicefoundation.songservice.configuration.TestsMappersConfig;
import com.epam.training.microservicefoundation.songservice.domain.dto.DeleteSongDTO;
import com.epam.training.microservicefoundation.songservice.domain.entity.Song;
import com.epam.training.microservicefoundation.songservice.service.mapper.DeleteSongMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestsMappersConfig.class)
class DeleteSongMapperTest {
  @Autowired
  private DeleteSongMapper deleteSongMapper;
  private final DeleteSongDTO deleteSongDTO = new DeleteSongDTO(1L, 123L);
  private final Song song =
      Song.builder().id(1L).resourceId(123L).name("test").album("mapper").artist("Map").lengthInSeconds(10).createdDate(
        LocalDateTime.now()).lastModifiedDate(LocalDateTime.now()).build();
  @Test
  void toDtoMapping() {
    DeleteSongDTO dto = deleteSongMapper.toDto(song);
    assertEquals(deleteSongDTO, dto);
  }

  @Test
  void toEntityMapping() {
    Song entity = deleteSongMapper.toEntity(deleteSongDTO);
    assertEquals(song.getId(), entity.getId());
    assertEquals(song.getResourceId(), entity.getResourceId());
  }
}
