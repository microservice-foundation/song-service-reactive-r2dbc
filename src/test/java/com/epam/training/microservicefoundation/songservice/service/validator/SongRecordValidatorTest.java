package com.epam.training.microservicefoundation.songservice.service.validator;

import com.epam.training.microservicefoundation.songservice.domain.SongRecord;
import com.epam.training.microservicefoundation.songservice.service.SongRecordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SongRecordValidatorTest {
    private SongRecordValidator validation;

    @BeforeEach
    void setup() {
        validation = new SongRecordValidator();
    }

    @Test
    void shouldBeValidate() {
        SongRecord songRecord = new SongRecord.Builder(1L, "ttest", "21:32").build();
        boolean isValid = validation.validate(songRecord);
        assertTrue(isValid);
    }

    @Test
    void shouldBeInvalidateWithInvalidResourceId() {
        SongRecord songRecord = new SongRecord.Builder(-1L, "ttest", "21:32").build();
        boolean isValid = validation.validate(songRecord);
        assertFalse(isValid);
    }

    @Test
    void shouldBeInvalidateWithInvalidName() {
        SongRecord songRecord = new SongRecord.Builder(1L, null, "21:32").build();
        boolean isValid = validation.validate(songRecord);
        assertFalse(isValid);
    }

    @Test
    void shouldBeInvalidateWithInvalidLength() {
        SongRecord songRecord = new SongRecord.Builder(1L, "test", null).build();
        boolean isValid = validation.validate(songRecord);
        assertFalse(isValid);
    }

    @Test
    void shouldBeInvalidateWithInvalidLengthContent() {
        SongRecord songRecord = new SongRecord.Builder(1L, "test", "12:-08").build();
        boolean isValid = validation.validate(songRecord);
        assertFalse(isValid);
    }
}