package com.epam.training.microservicefoundation.songservice.model;

import java.io.Serializable;

public class SongRecord implements Serializable {
    private static final long serialVersionUID = 17_11_2022_22_51L;
    private final long id;

    public SongRecord(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
