package com.example.logify.entities;

import java.util.ArrayList;

public class Category {
    private String id;
    private String name;
    private ArrayList<Topic> topics;

    public Category() {
    }

    public Category(String id, String name, ArrayList<Topic> topics) {
        this.id = id;
        this.name = name;
        this.topics = topics;
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

    public ArrayList<Topic> getTopics() {
        return topics;
    }

    public void setTopics(ArrayList<Topic> topics) {
        this.topics = topics;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", topics=" + topics +
                '}';
    }
}
