package com.epam.training.microservicefoundation.songservice.model;

import java.io.Serializable;

public class SongDTO implements Serializable {
  private static final long serialVersionUID = 17_11_2022_22_51L;
  private final long id;
  private final long resourceId;

  public SongDTO(long id, long resourceId) {
    this.id = id;
    this.resourceId = resourceId;
  }

  public long getId() {
    return id;
  }

  public long getResourceId() {
    return resourceId;
  }
}
