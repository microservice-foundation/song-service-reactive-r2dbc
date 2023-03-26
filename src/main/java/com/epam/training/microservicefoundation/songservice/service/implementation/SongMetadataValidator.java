package com.epam.training.microservicefoundation.songservice.service.implementation;

import com.epam.training.microservicefoundation.songservice.model.SongMetadata;
import com.epam.training.microservicefoundation.songservice.service.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Year;
import java.util.Objects;

@Component
public class SongMetadataValidator implements Validator<SongMetadata> {
  Logger log = LoggerFactory.getLogger(SongMetadataValidator.class);
  private static final String TIME_SPLITTER = ":";

  @Override
  public boolean validate(SongMetadata metadata) {
    log.info("Validating song metadata '{}' ", metadata);
    if (Objects.isNull(metadata) || Objects.isNull(metadata.getName()) || Objects.isNull(metadata.getLength())) {
      return false;
    }
    return isValidYear(metadata.getYear()) && isValidResourceId(metadata.getResourceId()) && isValidLength(metadata.getLength());
  }

  private boolean isValidResourceId(long resourceId) {
    return resourceId > 0L;
  }

  private boolean isValidYear(int year) {
    return year == 0 || (year > 1900 && Year.now().isAfter(Year.of(year)));
  }

  private boolean isValidLength(String length) {
    String[] lengthParts = length.split(TIME_SPLITTER);
    String minutes = lengthParts[0];
    String seconds = lengthParts[1];
    return lengthParts.length == 2 && Integer.parseInt(minutes) >= 0 && Integer.parseInt(seconds) >= 0;
  }
}
