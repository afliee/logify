package com.example.logify.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    private String id;
    private String name;
    private String description;
    private String image;
    private String createdDate;
    private ArrayList<Artist> artists;
    private ArrayList<String> artistIds;
    private ArrayList<Song> songs;



    /**
     * This is a constructor method to create a new instance of Topic class.
     * @param id
     * @param name
     * @param description
     * @param image
     * @param createdDate
     * @param artists
     * @param songs
     */
    public Album(String id, String name, String description, String image, String createdDate, ArrayList<Artist> artists, ArrayList<Song> songs) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.createdDate = createdDate;
        this.artists = artists;
        this.songs = songs;
    }



    public Album() {
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public void setArtists(ArrayList<Artist> artists) {
        this.artists = artists;
    }
    public void setArtistIds(ArrayList<String> artistIds) {
        this.artistIds = artistIds;
    }
    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }


    @Override
    public String toString() {
        return "Album{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", artists=" + artists +
                ", artistIds=" + artistIds +
                ", songs=" + songs +
                '}';
    }
}
