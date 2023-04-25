package com.example.logify.models;

import com.example.logify.entities.Topic;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class TopicModel extends Model {
    public TopicModel () {
        super();
    }

    public ArrayList<Topic> getTopics() {
        ArrayList<Topic> topics = new ArrayList<>();
        
        return topics;
    }
}
