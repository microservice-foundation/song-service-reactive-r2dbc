package com.epam.training.microservicefoundation.songservice.model.dto;

import com.epam.training.microservicefoundation.songservice.validator.ValidName;
import com.epam.training.microservicefoundation.songservice.validator.ValidYear;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveSongDTO implements Serializable {
  private static final long serialVersionUID = 2023_07_06_11_33L;

  @Min(1L) @Max(Long.MAX_VALUE)
  private long resourceId;

  @NotEmpty
  @ValidName
  private String name;
  private String artist;
  private String album;
  private long lengthInSeconds;

  @ValidYear
  private int year;
}
