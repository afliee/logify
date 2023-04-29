package com.example.logify.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Topic {
    private String id;
    private String title;
    private ArrayList<Playlist> playlists;
    private List<Playlist> data;


    public Topic() {
    }

    public Topic(String id, String title, ArrayList<Playlist> playlists) {
        this.id = id;
        this.title = title;
        this.playlists = playlists;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public List<Playlist> getData() {
        return data;
    }

    public void setData(List<Playlist> data) {
        this.data = data;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", this.id);
        result.put("title", this.title);
        result.put("data", this.data);
        return result;
    }
    @Override
    public String toString() {
        return "Topic{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", playlists=" + playlists +
                '}';
    }
}
