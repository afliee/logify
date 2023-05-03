package com.example.logify.entities;

import java.util.HashMap;
import java.util.Map;

public class Playlist {
    private String id;
    private String playlistName;
    private String description;
    private String image;
    private String userId;
    private String createdDate;

/**
     * This is a constructor method to create a new instance of Topic class.
     * @param id
     * @param playlistName
     * @param description
     * @param image
     * @param userId
     * @param createdDate
     */

    public Playlist(String id, String playlistName, String description, String image, String userId, String createdDate) {
        this.id = id;
        this.playlistName = playlistName;
        this.description = description;
        this.image = image;
        this.userId = userId;
        this.createdDate = createdDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return playlistName;
    }

    public void setTitle(String playlistName) {
        this.playlistName = Playlist.this.playlistName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", playlistName);
        result.put("description", description);
        result.put("image", image);
        result.put("userId", userId);
        result.put("createdDate", createdDate);
        return result;
    }
    @Override
    public String toString() {
        return "Playlist{" +
                "id='" + id + '\'' +
                ", playlistName='" + playlistName + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", userId='" + userId + '\'' +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }
}
