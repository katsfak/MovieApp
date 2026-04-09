package com.example.movieapp.data.model.details.reviews;

import com.google.gson.annotations.SerializedName;

public class ReviewsRemote {

    @SerializedName("author")
    private String author;

    @SerializedName("content")
    private String content;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("id")
    private String id;

    @SerializedName("updated_at")
    private String updatedAt;

    @SerializedName("url")
    private String url;

    public ReviewsRemote(String author, String content, String createdAt, String id, String updatedAt, String url) {
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
        this.id = id;
        this.updatedAt = updatedAt;
        this.url = url;
    }

    // Getters
    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getUrl() {
        return url;
    }

    // Setters
    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
