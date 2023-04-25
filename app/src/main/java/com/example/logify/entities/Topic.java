package com.example.logify.entities;

public class Topic {
    private String id;
    private String name;
    private String description;
    private String image;
    private String createdDate;

    /**
     * This is a constructor method to create a new instance of Topic class.
     * @param id
     * @param name
     * @param description
     * @param image
     * @param createdDate
     */
    public Topic(String id, String name, String description, String image, String createdDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.createdDate = createdDate;
    }

    public Topic() {
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
        return "Topic{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", createdDate='" + createdDate + '\'' +
                '}';
    }
}
