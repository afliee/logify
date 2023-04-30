package com.example.logify.entities;

import java.util.ArrayList;

public class Artist {
    private String id;
    private String name;
    private String bio;
    private String image;
    private String createdDate;
    private String playlistId;


    public Artist() {

    }


    public Artist(String id, String name, String bio, String image, String createdDate) {
        this.id = id;
        this.name = name;
        this.bio = bio;
        this.image = image;
        this.createdDate = createdDate;
    }

    public Artist(String id, String name, String image, String playlistId) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.playlistId = playlistId;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", image='" + image + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", playlistId='" + playlistId + '\'' +
                '}';
    }
}
