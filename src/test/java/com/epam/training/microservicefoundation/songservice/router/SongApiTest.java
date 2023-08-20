package com.epam.training.microservicefoundation.songservice.router;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.epam.training.microservicefoundation.songservice.configuration.WebFluxConfiguration;
import com.epam.training.microservicefoundation.songservice.mapper.DeleteSongMapper;
import com.epam.training.microservicefoundation.songservice.mapper.GetSongMapper;
import com.epam.training.microservicefoundation.songservice.mapper.SaveSongMapper;
import com.epam.training.microservicefoundation.songservice.model.dto.DeleteSongDTO;
import com.epam.training.microservicefoundation.songservice.model.dto.GetSongDTO;
import com.epam.training.microservicefoundation.songservice.model.dto.SaveSongDTO;
import com.epam.training.microservicefoundation.songservice.model.entity.Song;
import com.epam.training.microservicefoundation.songservice.repository.SongRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

@WebFluxTest
@DirtiesContext
@ContextConfiguration(classes = {WebFluxConfiguration.class})
@TestPropertySource(locations = "classpath:application.properties")
class SongApiTest {
  @MockBean
  private SongRepository repository;
  @MockBean
  private SaveSongMapper saveSongMapper;
  @MockBean
  private GetSongMapper getSongMapper;
  @MockBean
  private DeleteSongMapper deleteSongMapper;
  @Autowired
  private WebTestClient webTestClient;

  private final Song songEntity1 =
      Song.builder().resourceId(123L).name("Apollo").artist("Schwars").album("Greek").lengthInSeconds(20).year(2001).build();
  private final Song savedSongEntity1 =
      Song.builder().id(1L).resourceId(123L).name("Apollo").artist("Schwars").album("Greek").lengthInSeconds(20).year(2001).build();
  private final Song savedSongEntity2 =
      Song.builder().id(2L).resourceId(124L).name("Apollo2").artist("Schwars2").album("Greek2").lengthInSeconds(34).year(2002).build();
  private final GetSongDTO getSongDTO1 = new GetSongDTO(1L, 123L, "Apollo", "Schwars", "Greek", 20, 2001);
  private final SaveSongDTO saveSongDTO1 = new SaveSongDTO(123L, "Apollo", "Schwars", "Greek", 23, 2001);
  private final DeleteSongDTO deleteSongDTO1 = new DeleteSongDTO(1L, 123L);
  private final DeleteSongDTO deleteSongDTO2 = new DeleteSongDTO(2L, 124L);


  @Test
  void shouldSaveSong() {
    when(saveSongMapper.toEntity(any(SaveSongDTO.class))).thenReturn(songEntity1);
    when(repository.save(any(Song.class))).thenReturn(Mono.just(savedSongEntity1));
    when(getSongMapper.toDto(any(Song.class))).thenReturn(getSongDTO1);
    webTestClient
        .post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(saveSongDTO1))
        .exchange()
        .expectStatus().isCreated()
        .expectBody().jsonPath("$.id", notNullValue());
  }

  @Test
  void shouldThrowEntityExistsExceptionWhenSaveSong() {
    when(saveSongMapper.toEntity(any(SaveSongDTO.class))).thenThrow(DataIntegrityViolationException.class);
    webTestClient
        .post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(saveSongDTO1))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Song is already existed")
        .jsonPath("$.debugMessage").isEqualTo("Song with these parameters already exists");
  }

  @Test
  void shouldThrowValidationExceptionWhenSaveSongWithInvalidResourceId() {
    webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new SaveSongDTO(0L, "Apollo", "Schwars", "Greek", 12, 2001)))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");
  }

  @Test
  void shouldThrowValidationExceptionWhenSaveSongWithInvalidName() {
    webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new SaveSongDTO(123L, "", "Schwars", "Greek", 14, 2001)))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");
  }

  @Test
  void shouldThrowValidationExceptionWhenSaveSongWithInvalidYearOne() {
    webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new SaveSongDTO(123L, "Apollo", "Schwars", "Greek", 10, 2030)))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");
  }

  @Test
  void shouldThrowValidationExceptionWhenSaveSongWithInvalidYearTwo() {
    webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new SaveSongDTO(123L, "Apollo", "Schwars", "Greek", 9, 1000)))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");
  }

  @Test
  void shouldGetSongById() {
    when(repository.findById(anyLong())).thenReturn(Mono.just(savedSongEntity1));
    when(getSongMapper.toDto(any())).thenReturn(getSongDTO1);

    webTestClient.get().uri("/api/v1/songs/{id}", savedSongEntity1.getId())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(savedSongEntity1.getId())
        .jsonPath("$.resourceId").isEqualTo(savedSongEntity1.getResourceId())
        .jsonPath("$.name").isEqualTo(savedSongEntity1.getName())
        .jsonPath("$.lengthInSeconds").isEqualTo(savedSongEntity1.getLengthInSeconds())
        .jsonPath("$.album").isEqualTo(savedSongEntity1.getAlbum())
        .jsonPath("$.artist").isEqualTo(savedSongEntity1.getArtist());
  }

  @Test
  void shouldThrowExceptionWhenGetSongById() {
    when(repository.findById(anyLong())).thenReturn(Mono.empty());
    webTestClient.get().uri("/api/v1/songs/{id}", 124567L)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo("NOT_FOUND")
        .jsonPath("$.message").isEqualTo("Song is not found")
        .jsonPath("$.debugMessage").isEqualTo("Song with id=124567 is not found");
  }

  @Test
  void shouldDeleteSongsByIds() {
    when(repository.findById(savedSongEntity1.getId())).thenReturn(Mono.just(savedSongEntity1));
    when(repository.delete(savedSongEntity1)).thenReturn(Mono.empty());
    when(deleteSongMapper.toDto(savedSongEntity1)).thenReturn(deleteSongDTO1);

    when(repository.findById(savedSongEntity2.getId())).thenReturn(Mono.just(savedSongEntity2));
    when(repository.delete(savedSongEntity2)).thenReturn(Mono.empty());
    when(deleteSongMapper.toDto(savedSongEntity2)).thenReturn(deleteSongDTO2);

    webTestClient.delete().uri(uriBuilder -> uriBuilder
            .path("/api/v1/songs")
            .queryParam("id", savedSongEntity1.getId() + "," + savedSongEntity2.getId())
            .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$[*].id").value(containsInAnyOrder(
            is((int) savedSongEntity1.getId()),
            is((int) savedSongEntity2.getId())))
        .jsonPath("$[*].resourceId").value(containsInAnyOrder(
            is((int) savedSongEntity1.getResourceId()),
            is((int) savedSongEntity2.getResourceId())));

    webTestClient.delete().uri(uriBuilder -> uriBuilder
            .path("/api/v1/songs")
            .queryParam("id", savedSongEntity1.getId() + ", " + savedSongEntity2.getId())
            .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$[*].id").value(containsInAnyOrder(
            is((int) savedSongEntity1.getId()),
            is((int) savedSongEntity2.getId())))
        .jsonPath("$[*].resourceId").value(containsInAnyOrder(
            is((int) savedSongEntity1.getResourceId()),
            is((int) savedSongEntity2.getResourceId())));
  }

  @Test
  void shouldThrowErrorWhenDeleteSongByEmptyId() {
    webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1/songs").queryParam("id", "").build())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");
  }

  @Test
  void shouldThrowErrorWhenDeleteSongByInvalidIds() {
    webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1/songs").queryParam("id", "a,b,c").build())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");

    webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1/songs").queryParam("id", "a-b;c").build())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");
  }

  @Test
  void shouldDeleteSongByIds() {
    when(repository.findById(savedSongEntity1.getId())).thenReturn(Mono.just(savedSongEntity1));
    when(repository.delete(savedSongEntity1)).thenReturn(Mono.empty());
    when(deleteSongMapper.toDto(savedSongEntity1)).thenReturn(deleteSongDTO1);

    webTestClient.delete().uri(uriBuilder -> uriBuilder
            .path("/api/v1/songs")
            .queryParam("id", savedSongEntity1.getId())
            .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$[*].id").isEqualTo((int) savedSongEntity1.getId())
        .jsonPath("$[*].resourceId").isEqualTo((int) savedSongEntity1.getResourceId());
  }

  @Test
  void shouldDeleteSongsByResourceIds() {
    when(repository.findById(savedSongEntity1.getResourceId())).thenReturn(Mono.just(savedSongEntity1));
    when(repository.delete(savedSongEntity1)).thenReturn(Mono.empty());
    when(deleteSongMapper.toDto(savedSongEntity1)).thenReturn(deleteSongDTO1);

    when(repository.findById(savedSongEntity2.getResourceId())).thenReturn(Mono.just(savedSongEntity2));
    when(repository.delete(savedSongEntity2)).thenReturn(Mono.empty());
    when(deleteSongMapper.toDto(savedSongEntity2)).thenReturn(deleteSongDTO2);

    webTestClient.delete().uri(uriBuilder -> uriBuilder
            .path("/api/v1/songs/by-resource-id")
            .queryParam("id", savedSongEntity1.getResourceId() + "," + savedSongEntity2.getResourceId())
            .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$[*].id").value(containsInAnyOrder(
            is((int) savedSongEntity1.getId()),
            is((int) savedSongEntity2.getId())))
        .jsonPath("$[*].resourceId").value(containsInAnyOrder(
            is((int) savedSongEntity1.getResourceId()),
            is((int) savedSongEntity2.getResourceId())));

    webTestClient.delete().uri(uriBuilder -> uriBuilder
            .path("/api/v1/songs/by-resource-id")
            .queryParam("id", savedSongEntity1.getResourceId() + ", " + savedSongEntity2.getResourceId())
            .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$[*].id").value(containsInAnyOrder(
            is((int) savedSongEntity1.getId()),
            is((int) savedSongEntity2.getId())))
        .jsonPath("$[*].resourceId").value(containsInAnyOrder(
            is((int) savedSongEntity1.getResourceId()),
            is((int) savedSongEntity2.getResourceId())));
  }

  @Test
  void shouldThrowErrorWhenDeleteSongByEmptyResourceId() {
    webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1/songs/by-resource-id").queryParam("id", "").build())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");
  }

  @Test
  void shouldThrowErrorWhenDeleteSongByInvalidResourceIds() {
    webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1/songs/by-resource-id").queryParam("id", "a,b,c").build())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");

    webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1/songs/by-resource-id").queryParam("id", "a-b;c").build())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");
  }

  @Test
  void shouldDeleteSongByResourceIds() {
    when(repository.findById(savedSongEntity1.getResourceId())).thenReturn(Mono.just(savedSongEntity1));
    when(repository.delete(savedSongEntity1)).thenReturn(Mono.empty());
    when(deleteSongMapper.toDto(savedSongEntity1)).thenReturn(deleteSongDTO1);

    webTestClient.delete().uri(uriBuilder -> uriBuilder
            .path("/api/v1/songs")
            .queryParam("id", savedSongEntity1.getResourceId())
            .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$[*].id").isEqualTo((int) savedSongEntity1.getId())
        .jsonPath("$[*].resourceId").isEqualTo((int) savedSongEntity1.getResourceId());
  }

  @Test
  void shouldUpdateSong() {
    when(saveSongMapper.toEntity(any())).thenReturn(songEntity1);
    when(repository.save(any())).thenReturn(Mono.just(savedSongEntity1));
    when(getSongMapper.toDto(any())).thenReturn(getSongDTO1);


    webTestClient.put().uri("/api/v1/songs/{id}", savedSongEntity1.getId())
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(saveSongDTO1))
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(savedSongEntity1.getId())
        .jsonPath("$.resourceId").isEqualTo(savedSongEntity1.getResourceId())
        .jsonPath("$.name").isEqualTo(savedSongEntity1.getName())
        .jsonPath("$.lengthInSeconds").isEqualTo(savedSongEntity1.getLengthInSeconds())
        .jsonPath("$.album").isEqualTo(savedSongEntity1.getAlbum())
        .jsonPath("$.artist").isEqualTo(savedSongEntity1.getArtist());
  }

  @Test
  void shouldThrowEntityExistsExceptionWhenUpdateSong() {
    when(saveSongMapper.toEntity(any(SaveSongDTO.class))).thenThrow(DataIntegrityViolationException.class);
    webTestClient
        .put().uri("/api/v1/songs/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(saveSongDTO1))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Song is already existed")
        .jsonPath("$.debugMessage").isEqualTo("Song with these parameters already exists");
  }

  @Test
  void shouldThrowValidationExceptionWhenUpdateSongWithInvalidResourceId() {
    webTestClient
        .put().uri("/api/v1/songs/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new SaveSongDTO(0L, "Apollo", "Schwars", "Greek", 9, 2001)))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");
  }

  @Test
  void shouldThrowValidationExceptionWhenUpdateSongWithInvalidName() {
    webTestClient
        .put().uri("/api/v1/songs/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new SaveSongDTO(123L, "", "Schwars", "Greek", 13, 2001)))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");
  }

  @Test
  void shouldThrowValidationExceptionWhenUpdateSongWithInvalidYearOne() {
    webTestClient
        .put().uri("/api/v1/songs/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new SaveSongDTO(123L, "Apollo", "Schwars", "Greek", 15, 2030)))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");
  }

  @Test
  void shouldThrowValidationExceptionWhenUpdateSongWithInvalidYearTwo() {
    webTestClient
        .put().uri("/api/v1/songs/{id}", 1L)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(new SaveSongDTO(123L, "Apollo", "Schwars", "Greek", 19, 1000)))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request");
  }
}
