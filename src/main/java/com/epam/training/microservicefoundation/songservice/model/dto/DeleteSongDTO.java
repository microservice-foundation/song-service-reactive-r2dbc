package com.epam.training.microservicefoundation.songservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteSongDTO {
  private long id;
  private long resourceId;
}
