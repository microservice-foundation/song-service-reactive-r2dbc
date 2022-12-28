package com.epam.training.microservicefoundation.songservice.api;

import com.epam.training.microservicefoundation.songservice.domain.SongRecord;
import com.epam.training.microservicefoundation.songservice.repository.PostgresExtension;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(value = {PostgresExtension.class})
@TestPropertySource(locations = "classpath:application.yaml")
class SongControllerTest {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldSaveSong() throws Exception {
        SongRecord songRecord = new SongRecord.Builder(123L, "Hello World", "54:21")
                .album("Tech").artist("Arnold Kim").year(2009).build();
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songRecord)));

        perform.andExpect(status().isCreated());
        perform.andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void shouldThrowValidationExceptionWhenSaveSongWithInvalidResourceId() throws Exception {
        SongRecord songRecord = new SongRecord.Builder(-1L, "Hello World", "54:21")
                .album("Tech").artist("Arnold Kim").year(2009).build();
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songRecord)));

        perform.andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowValidationExceptionWhenSaveSongWithInvalidName() throws Exception {
        SongRecord songRecord = new SongRecord.Builder(1L, null, "54:21")
                .album("Tech").artist("Arnold Kim").year(2009).build();
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songRecord)));

        perform.andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowValidationExceptionWhenSaveSongWithInvalidLength() throws Exception {
        SongRecord songRecord = new SongRecord.Builder(1L, null, "-54:21")
                .album("Tech").artist("Arnold Kim").year(2009).build();
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songRecord)));

        perform.andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowValidationExceptionWhenSaveSongWithInvalidYearOne() throws Exception {
        SongRecord songRecord = new SongRecord.Builder(1L, "test", "5:21")
                .album("Tech").artist("Arnold Kim").year(2023).build();
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songRecord)));

        perform.andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowValidationExceptionWhenSaveSongWithInvalidYearTwo() throws Exception {
        SongRecord songRecord = new SongRecord.Builder(1L, "test", "5:21")
                .album("Tech").artist("Arnold Kim").year(1899).build();
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songRecord)));

        perform.andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetSongMetadata() throws Exception {
        // save a song metadata
        SongRecord songRecord = new SongRecord.Builder(129_888_999L, "test", "5:21")
                .album("Tech").artist("Arnold Kim").build();
        MvcResult postResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songRecord))).andReturn();

        ResultActions getResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/songs/{id}", getValueOf("id", postResult
                .getResponse().getContentAsString())));
        getResult.andExpect(status().isOk());
        getResult.andExpect(jsonPath("$.resourceId", is((int) songRecord.getResourceId())));
        getResult.andExpect(jsonPath("$.name", is(songRecord.getName())));
        getResult.andExpect(jsonPath("$.length", is(songRecord.getLength())));
        getResult.andExpect(jsonPath("$.album", is(songRecord.getAlbum())));
        getResult.andExpect(jsonPath("$.artist", is(songRecord.getArtist())));
    }

    @Test
    void shouldThrowExceptionWhenGetSongMetadata() throws Exception {
        ResultActions getResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/songs/{id}", 124567L));
        getResult.andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteSongMetadata() throws Exception {
        SongRecord songRecord1 = new SongRecord.Builder(130_888_999L, "test", "5:21")
                .album("Tech").artist("Arnold Kim").build();
        MvcResult postResult1 = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songRecord1))).andReturn();

        SongRecord songRecord2 = new SongRecord.Builder(131_888_999L, "test", "5:21")
                .album("Tech").artist("Arnold Kim").build();
        MvcResult postResult2 = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(songRecord2))).andReturn();

        String songRecord1Id = getValueOf("id",
                postResult1.getResponse().getContentAsString());
        String songRecord2Id = getValueOf("id", postResult2.getResponse().getContentAsString());
        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/v1/songs").param("id", songRecord1Id, songRecord2Id));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$[*].id", contains(Integer.valueOf(songRecord1Id), Integer.valueOf(songRecord2Id))));
    }


    @Test
    void shouldThrowValidationExceptionWhenDeleteSongsByIds() throws Exception {
        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/v1/songs").param("id", new String[200]));

        result.andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowValidationExceptionWhenDeleteResourceByNegativeIds() throws Exception {
        ResultActions result = mockMvc
                .perform(MockMvcRequestBuilders.delete("/api/v1/songs").param("id", "-1", "-3L"));

        result.andExpect(status().isBadRequest());
    }

    private String getValueOf(String field, String jsonSource) throws JSONException {
        return new JSONObject(jsonSource).get(field).toString();
    }
}
