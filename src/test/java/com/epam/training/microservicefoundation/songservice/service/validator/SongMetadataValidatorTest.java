package com.epam.training.microservicefoundation.songservice.service.validator;

import com.epam.training.microservicefoundation.songservice.model.SongMetadata;
import com.epam.training.microservicefoundation.songservice.service.implementation.SongRecordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SongMetadataValidatorTest {
    private SongRecordValidator validation;

    @BeforeEach
    void setup() {
        validation = new SongRecordValidator();
    }

    @Test
    void shouldBeValidate() {
        SongMetadata songMetadata = new SongMetadata.Builder(1L, "ttest", "21:32").build();
        boolean isValid = validation.validate(songMetadata);
        assertTrue(isValid);
    }

    @Test
    void shouldBeInvalidateWithInvalidResourceId() {
        SongMetadata songMetadata = new SongMetadata.Builder(-1L, "ttest", "21:32").build();
        boolean isValid = validation.validate(songMetadata);
        assertFalse(isValid);
    }

    @Test
    void shouldBeInvalidateWithInvalidName() {
        SongMetadata songMetadata = new SongMetadata.Builder(1L, null, "21:32").build();
        boolean isValid = validation.validate(songMetadata);
        assertFalse(isValid);
    }

    @Test
    void shouldBeInvalidateWithInvalidLength() {
        SongMetadata songMetadata = new SongMetadata.Builder(1L, "test", null).build();
        boolean isValid = validation.validate(songMetadata);
        assertFalse(isValid);
    }

    @Test
    void shouldBeInvalidateWithInvalidLengthContent() {
        SongMetadata songMetadata = new SongMetadata.Builder(1L, "test", "12:-08").build();
        boolean isValid = validation.validate(songMetadata);
        assertFalse(isValid);
    }
}