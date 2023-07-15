package com.epam.training.microservicefoundation.songservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteSongDTO {
  private static final long serialVersionUID = 2023_07_15_15_53L;
  private long id;
  private long resourceId;
}
