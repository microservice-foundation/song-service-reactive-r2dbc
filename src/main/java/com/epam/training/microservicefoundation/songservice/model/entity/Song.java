package com.epam.training.microservicefoundation.songservice.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table("SONGS")
public class Song implements Serializable {
  public static final long serialVersionUID = 2022_10_22_14_26L;
  @Id
  private long id;
  private long resourceId;
  private String name;
  private String artist;
  private String album;
  private long lengthInSeconds;
  private int year;
  @CreatedDate
  private LocalDateTime createdDate;
  @LastModifiedDate
  private LocalDateTime lastModifiedDate;
}
