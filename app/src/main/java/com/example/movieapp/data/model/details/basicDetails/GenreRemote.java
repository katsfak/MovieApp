package com.example.movieapp.data.model.details.basicDetails;

import com.google.gson.annotations.SerializedName;

public class GenreRemote {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public GenreRemote(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
