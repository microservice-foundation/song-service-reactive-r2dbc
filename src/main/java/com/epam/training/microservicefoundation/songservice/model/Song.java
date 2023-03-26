package com.epam.training.microservicefoundation.songservice.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("SONGS")
public class Song implements Serializable {
  public static final long serialVersionUID = 2022_10_22_14_26L;
  @Id
  private long id;
  private long resourceId;
  private String name;
  private String artist;
  private String album;
  private String length;
  private int year;
  @CreatedDate
  private LocalDateTime createdDate;
  @LastModifiedDate
  private LocalDateTime lastModifiedDate;

  protected Song() {
  }

  private Song(Builder builder) {
    this.resourceId = builder.resourceId;
    this.name = builder.name;
    this.artist = builder.artist;
    this.album = builder.album;
    this.length = builder.length;
    this.year = builder.year;
    this.id = builder.id;
  }

  public static class Builder {
    private final long resourceId;
    private final String name;
    private final String length;
    private String artist;
    private String album;
    private int year;
    private long id;

    public Builder(long resourceId, String name, String length) {
      this.resourceId = resourceId;
      this.name = name;
      this.length = length;
    }

    public Builder artist(String artist) {
      this.artist = artist;
      return this;
    }

    public Builder album(String album) {
      this.album = album;
      return this;
    }

    public Builder year(int year) {
      this.year = year;
      return this;
    }

    public Builder id(long id) {
      this.id = id;
      return this;
    }

    public Song build() {
      return new Song(this);
    }
  }

  public long getId() {
    return id;
  }

  public long getResourceId() {
    return resourceId;
  }

  public String getName() {
    return name;
  }

  public String getArtist() {
    return artist;
  }

  public String getAlbum() {
    return album;
  }

  public String getLength() {
    return length;
  }

  public int getYear() {
    return year;
  }

  public LocalDateTime getCreatedDate() {
    return createdDate;
  }

  public LocalDateTime getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setArtist(String artist) {
    this.artist = artist;
  }

  public void setAlbum(String album) {
    this.album = album;
  }

  public void setLength(String length) {
    this.length = length;
  }

  public void setYear(int year) {
    this.year = year;
  }

  @Override
  public int hashCode() {
    int result = 17;
    if (id != 0L) {
      result += 31 * Long.hashCode(id);
    }
    if (resourceId != 0L) {
      result += 31 * Long.hashCode(resourceId);
    }
    if (name != null) {
      result += 31 * name.hashCode();
    }
    if (length != null) {
      result += 31 * length.hashCode();
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || obj.getClass() != getClass()) {
      return false;
    }
    Song song = (Song) obj;
    return Objects.equals(this.id, song.id) &&
        Objects.equals(this.resourceId, song.resourceId) &&
        Objects.equals(this.name, song.name) &&
        Objects.equals(this.length, song.length);
  }
}
