package com.example.logify.entities;

public class Album {
    private String id;
    private String name;
    private String artistId;
    private String image;
    private String sortDescription;
    private int duration;
    private int numberOfSongs;
    private int numberOfLikes;

    public Album() {
    }

    public Album(String id, String name, String artistId, String image, String sortDescription, int duration, int numberOfSongs, int numberOfLikes) {
        this.id = id;
        this.name = name;
        this.artistId = artistId;
        this.image = image;
        this.sortDescription = sortDescription;
        this.duration = duration;
        this.numberOfSongs = numberOfSongs;
        this.numberOfLikes = numberOfLikes;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSortDescription() {
        return sortDescription;
    }

    public void setSortDescription(String sortDescription) {
        this.sortDescription = sortDescription;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getNumberOfSongs() {
        return numberOfSongs;
    }

    public void setNumberOfSongs(int numberOfSongs) {
        this.numberOfSongs = numberOfSongs;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artistId='" + artistId + '\'' +
                ", image='" + image + '\'' +
                ", sortDescription='" + sortDescription + '\'' +
                ", duration=" + duration +
                ", numberOfSongs=" + numberOfSongs +
                ", numberOfLikes=" + numberOfLikes +
                '}';
    }
}
