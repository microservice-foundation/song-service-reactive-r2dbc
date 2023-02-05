package com.epam.training.microservicefoundation.songservice.service.implementation;

import com.epam.training.microservicefoundation.songservice.model.SongMetadata;
import com.epam.training.microservicefoundation.songservice.service.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.Objects;
@Component
public class SongRecordValidator implements Validator<SongMetadata> {
    Logger log = LoggerFactory.getLogger(SongRecordValidator.class);
    private static final String TIME_SPLITTER = ":";
    @Override
    public boolean validate(SongMetadata record) {
        log.info("Validating song record '{}' ", record);
        if(record == null) {
            return false;
        }
        if(record.getResourceId() <= 0L || Objects.isNull(record.getName()) || Objects.isNull(record.getLength())) {
            return false;
        }

        if(record.getYear() != 0 && (record.getYear() < 1900 || Year.now().isBefore(Year.of(record.getYear())))) {
            return false;
        }

        String[] lengthParts = record.getLength().split(TIME_SPLITTER);
        String minutes = lengthParts[0];
        String seconds = lengthParts[1];
        return lengthParts.length == 2 && Integer.parseInt(minutes) >= 0 && Integer.parseInt(seconds) >= 0;
    }
}
