package com.example.logify.entities;

import java.io.Serializable;

public class Song implements Serializable {
    private String id;
    private String name;
    private String artistId;
    private String resource;
    private String releaseDate;

    public Song(String id, String name, String artistId, String resource, String releaseDate) {
        this.id = id;
        this.name = name;
        this.artistId = artistId;
        this.resource = resource;
        this.releaseDate = releaseDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String
    toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artistId='" + artistId + '\'' +
                ", resource='" + resource + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}
