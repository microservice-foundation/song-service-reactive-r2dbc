package com.epam.training.microservicefoundation.songservice.domain.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetSongDTO extends AuditableDTO implements Serializable {
  private static final long serialVersionUID = 2023_07_06_11_33L;
  private long id;
  private long resourceId;
  private String name;
  private String artist;
  private String album;
  private long lengthInSeconds;
  private int year;
}
