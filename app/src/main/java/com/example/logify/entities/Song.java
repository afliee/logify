package com.example.logify.entities;

public class Song {
    private String id;
    private String name;
    private String artistId;
    private int duration;
    private String releaseDate;

    public Song(String id, String name, String artistId, int duration, String releaseDate) {
        this.id = id;
        this.name = name;
        this.artistId = artistId;
        this.duration = duration;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Song{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artistId='" + artistId + '\'' +
                ", duration=" + duration +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}
