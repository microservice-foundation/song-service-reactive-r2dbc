package com.epam.training.microservicefoundation.songservice.api;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.epam.training.microservicefoundation.songservice.model.SongDTO;
import com.epam.training.microservicefoundation.songservice.model.SongMetadata;
import com.epam.training.microservicefoundation.songservice.repository.PostgresExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest
@ExtendWith(value = {PostgresExtension.class})
@AutoConfigureWebTestClient
@TestPropertySource(locations = "classpath:application.properties")
class SongControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Test
  void shouldSaveSong(){
    SongMetadata songMetadata = new SongMetadata.Builder(123L, "Hello World", "54:21")
        .album("Tech").artist("Arnold Kim").year(2009).build();
    webTestClient
        .post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(songMetadata))
        .exchange()
        .expectStatus().isCreated()
        .expectBody().jsonPath("$.id", notNullValue());
  }

  @Test
  void shouldThrowValidationExceptionWhenSaveSongWithInvalidResourceId() {
    SongMetadata songMetadata = new SongMetadata.Builder(-1L, "Hello World", "54:21")
        .album("Tech").artist("Arnold Kim").year(2009).build();

    WebTestClient.BodyContentSpec bodyContentSpec = webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(songMetadata))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request")
        .jsonPath("$.debugMessage").isEqualTo("Saving invalid song record");
  }

  @Test
  void shouldThrowValidationExceptionWhenSaveSongWithInvalidName() {
    SongMetadata songMetadata = new SongMetadata.Builder(1L, null, "54:21")
        .album("Tech").artist("Arnold Kim").year(2009).build();

    webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(songMetadata))
        .exchange()
        .expectStatus().isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request")
        .jsonPath("$.debugMessage").isEqualTo("Saving invalid song record");
  }

  @Test
  void shouldThrowValidationExceptionWhenSaveSongWithInvalidLength() {
    SongMetadata songMetadata = new SongMetadata.Builder(1L, null, "-54:21")
        .album("Tech").artist("Arnold Kim").year(2009).build();

    webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(songMetadata))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request")
        .jsonPath("$.debugMessage").isEqualTo("Saving invalid song record");
  }

  @Test
  void shouldThrowValidationExceptionWhenSaveSongWithInvalidYearOne() {
    SongMetadata songMetadata = new SongMetadata.Builder(1L, "test", "5:21")
        .album("Tech").artist("Arnold Kim").year(2099).build();

    webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(songMetadata))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request")
        .jsonPath("$.debugMessage").isEqualTo("Saving invalid song record");;
  }

  @Test
  void shouldThrowValidationExceptionWhenSaveSongWithInvalidYearTwo() {
    SongMetadata songMetadata = new SongMetadata.Builder(1L, "test", "5:21")
        .album("Tech").artist("Arnold Kim").year(1899).build();

    webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(songMetadata))
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request")
        .jsonPath("$.debugMessage").isEqualTo("Saving invalid song record");;
  }

  @Test
  void shouldGetSongMetadata() {
    // save a song metadata
    SongMetadata songMetadata = new SongMetadata.Builder(129_888_999L, "test", "5:21")
        .album("Tech").artist("Arnold Kim").build();

    SongDTO postResult = webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(songMetadata))
        .exchange()
        .expectStatus()
        .isCreated().expectBody(SongDTO.class).returnResult().getResponseBody();

    webTestClient.get().uri("/api/v1/songs/{id}", postResult.getId())
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(postResult.getId())
        .jsonPath("$.resourceId").isEqualTo(songMetadata.getResourceId())
        .jsonPath("$.name").isEqualTo(songMetadata.getName())
        .jsonPath("$.length").isEqualTo(songMetadata.getLength())
        .jsonPath("$.album").isEqualTo(songMetadata.getAlbum())
        .jsonPath("$.artist").isEqualTo(songMetadata.getArtist());
  }

  @Test
  void shouldThrowExceptionWhenGetSongMetadata() throws Exception {
    webTestClient.get().uri("/api/v1/songs/{id}", 124567L)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectBody()
        .jsonPath("$.status").isEqualTo("NOT_FOUND")
        .jsonPath("$.message").isEqualTo("Song metadata is not found")
        .jsonPath("$.debugMessage").isEqualTo("Song is not found with id '124567'");
  }

  @Test
  void shouldDeleteSongMetadataById() {
    SongMetadata songMetadata1 = new SongMetadata.Builder(130_888_999L, "test", "5:21")
        .album("Tech").artist("Arnold Kim").build();

    SongDTO postResult1 = webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(songMetadata1))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(SongDTO.class)
        .returnResult()
        .getResponseBody();

    SongMetadata songMetadata2 = new SongMetadata.Builder(131_888_999L, "test", "5:21")
        .album("Tech").artist("Arnold Kim").build();

    SongDTO postResult2 = webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(songMetadata2))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(SongDTO.class)
        .returnResult()
        .getResponseBody();

    webTestClient.delete().uri(uriBuilder -> uriBuilder
            .path("/api/v1/songs")
            .queryParam("id", postResult1.getId() + "," + postResult2.getId())
            .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$[*].id").value(containsInAnyOrder(
            is((int) postResult1.getId()),
            is((int) postResult2.getId())))
        .jsonPath("$[*].resourceId").value(containsInAnyOrder(
            is((int) postResult1.getResourceId()),
            is((int) postResult2.getResourceId())));
  }

  @Test
  void shouldReturnEmptyWhenDeleteSongMetadataByNegativeResourceIds() {
    webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1/songs/by-resource-id").queryParam("id", "-1,-3").build())
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void shouldReturnEmptyWhenDeleteSongMetadataByNegativeIds() {
    webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1/songs").queryParam("id", "-1,-3").build())
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  void shouldThrowErrorWhenDeleteSongMetadataByEmptyId() {
    webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1/songs").queryParam("id", "").build())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request")
        .jsonPath("$.debugMessage").isEqualTo("For input string: \"\"");
  }

  @Test
  void shouldDeleteSongMetadataByResourceId() {
    SongMetadata songMetadata1 = new SongMetadata.Builder(130_888_999L, "test", "5:21")
        .album("Tech").artist("Arnold Kim").build();

    SongDTO postResult1 = webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(songMetadata1))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(SongDTO.class)
        .returnResult()
        .getResponseBody();

    SongMetadata songMetadata2 = new SongMetadata.Builder(131_888_999L, "test", "5:21")
        .album("Tech").artist("Arnold Kim").build();

    SongDTO postResult2 = webTestClient.post().uri("/api/v1/songs")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(songMetadata2))
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(SongDTO.class)
        .returnResult()
        .getResponseBody();

    webTestClient.delete().uri(uriBuilder -> uriBuilder
            .path("/api/v1/songs/by-resource-id")
            .queryParam("id", postResult1.getResourceId() + "," + postResult2.getResourceId())
            .build())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$[*].id").value(containsInAnyOrder(
            is((int) postResult1.getId()),
            is((int) postResult2.getId())))
        .jsonPath("$[*].resourceId").value(containsInAnyOrder(
            is((int) postResult1.getResourceId()),
            is((int) postResult2.getResourceId())));
  }

  @Test
  void shouldThrowErrorWhenDeleteSongMetadataByEmptyResourceId() {
    webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/api/v1/songs/by-resource-id").queryParam("id", "").build())
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.status").isEqualTo("BAD_REQUEST")
        .jsonPath("$.message").isEqualTo("Invalid request")
        .jsonPath("$.debugMessage").isEqualTo("For input string: \"\"");
  }
}
