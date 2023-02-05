package com.epam.training.microservicefoundation.songservice.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Song implements Serializable {
    public static final long serialVersionUID = 2022_10_22_14_26L;
    @Id
    @GeneratedValue(generator = "song_sequence", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "song_sequence", sequenceName = "song_sequence", allocationSize = 5, initialValue = 1)
    private long id;
    @Column(nullable = false, unique = true)
    private long resourceId;
    @Column(length = 100, nullable = false)
    private String name;
    @Column(length = 50)
    private String artist;
    @Column(length = 50)
    private String album;
    @Column(length = 10, nullable = false)
    private String length;
    private int year;
    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    protected Song() {}
    private Song(Builder builder) {
        this.resourceId = builder.resourceId;
        this.name = builder.name;
        this.artist = builder.artist;
        this.album = builder.album;
        this.length = builder.length;
        this.year = builder.year;
    }

    public static class Builder {
        private final long resourceId;
        private final String name;
        private final String length;
        private String artist;
        private String album;
        private int year;

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
        if(id != 0L) {
            result += 31 * Long.hashCode(id);
        }
        if(resourceId != 0L) {
            result += 31 * Long.hashCode(resourceId);
        }
        if(name != null) {
            result += 31 * name.hashCode();
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Song song = (Song) obj;
        return Objects.equals(this.id, song.id) &&
                Objects.equals(this.resourceId, song.resourceId) &&
                Objects.equals(this.name, song.name);
    }
}
