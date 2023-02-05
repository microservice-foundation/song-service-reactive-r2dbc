package com.epam.training.microservicefoundation.songservice.base;

import com.epam.training.microservicefoundation.songservice.api.SongController;
import com.epam.training.microservicefoundation.songservice.api.SongExceptionHandler;
import com.epam.training.microservicefoundation.songservice.model.SongNotFoundException;
import com.epam.training.microservicefoundation.songservice.model.SongMetadata;
import com.epam.training.microservicefoundation.songservice.model.SongRecord;
import com.epam.training.microservicefoundation.songservice.service.SongService;
import com.epam.training.microservicefoundation.songservice.service.Validator;
import com.epam.training.microservicefoundation.songservice.service.implementation.SongRecordValidator;
import io.restassured.config.EncoderConfig;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.config.RestAssuredMockMvcConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT) // https://stackoverflow.com/questions/42947613/how-to-resolve-unneccessary-stubbing-exception
@ContextConfiguration(classes = RestBase.RestBaseConfiguration.class)
public abstract class RestBase {
    private SongController songController;
    @Autowired
    SongExceptionHandler songExceptionHandler;
    @MockBean
    SongService songService;

    private Validator<SongMetadata> songRecordValidator;

    @BeforeEach
    public void setup() {
        songRecordValidator = new SongRecordValidator();
        when(songService.getById(199L)).thenReturn(new SongMetadata.Builder(1L, "Saturday", "03:40")
                .album("2023").artist("John Biden").year(1990).build());

        when(songService.getById(1999L)).thenThrow(new SongNotFoundException("Song was not found with id '1999'"));
        when(songService.deleteByIds(new long[]{199L})).thenReturn(Collections.singletonList(new SongRecord(199L)));
        when(songService.deleteByIds(new long[0])).thenThrow(new IllegalArgumentException("Id param was not " +
                "validated, check your ids"));

        when(songService.deleteByResourceIds(new long[]{1L})).thenReturn(Collections
                .singletonList(new SongRecord(199L)));

        when(songService.deleteByResourceIds(new long[0])).thenThrow(new IllegalArgumentException("Id param was " +
                "not validated, check your ids"));

        when(songService.save(argThat(argument -> TRUE.equals(songRecordValidator.validate(argument)))))
                .thenReturn(new SongRecord(199L));

        when(songService.save(argThat(argument -> FALSE.equals(songRecordValidator.validate(argument)))))
                .thenThrow(new IllegalArgumentException("Saving invalid song record"));

        songController = new SongController(songService);
        EncoderConfig encoderConfig = new EncoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false);
        RestAssuredMockMvc.config = new RestAssuredMockMvcConfig().encoderConfig(encoderConfig);
        RestAssuredMockMvc.standaloneSetup(MockMvcBuilders.standaloneSetup(songController)
                .setControllerAdvice(songExceptionHandler));
    }
    @TestConfiguration
    static class RestBaseConfiguration {

        @Bean
        SongExceptionHandler songExceptionHandler() {
            return new SongExceptionHandler();
        }
    }
}
