package com.epam.training.microservicefoundation.songservice.model.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetSongDTO implements Serializable {
  private static final long serialVersionUID = 2023_07_06_11_33L;
  private long id;
  private long resourceId;
  private String name;
  private String artist;
  private String album;
  private long lengthInSeconds;
  private int year;
}
