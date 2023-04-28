package com.example.logify.entities;

import java.util.ArrayList;

public class Topic {
    private String id;
    private String name;
    private ArrayList<Playlist> playlists;

    public Topic() {
    }

    public Topic(String id, String name, ArrayList<Playlist> playlists) {
        this.id = id;
        this.name = name;
        this.playlists = playlists;
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

    public ArrayList<Playlist> getTopics() {
        return playlists;
    }

    public void setTopics(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", topics=" + playlists +
                '}';
    }
}
