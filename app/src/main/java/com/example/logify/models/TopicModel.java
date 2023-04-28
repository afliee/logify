package com.example.logify.models;

import com.example.logify.entities.Playlist;

import java.util.ArrayList;

public class TopicModel extends Model {
    private static final String COLLECTION_NAME = "topics";
    public TopicModel () {
        super();
    }

    public ArrayList<Playlist> getTopics() {
        ArrayList<Playlist> playlists = new ArrayList<>();
        
        return playlists;
    }
}
