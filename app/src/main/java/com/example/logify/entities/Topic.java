package com.example.logify.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Topic {
    private String id;
    private String title;
    private ArrayList<Album> albums;
    private List<Album> data;


    public Topic() {
    }

    public Topic(String id, String title, ArrayList<Album> albums) {
        this.id = id;
        this.title = title;
        this.albums = albums;
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

    public List<Album> getData() {
        return data;
    }

    public void setData(List<Album> data) {
        this.data = data;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(ArrayList<Album> albums) {
        this.albums = albums;
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
                ", playlists=" + albums +
                '}';
    }
}
