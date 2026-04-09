package com.example.movieapp.data.model.details.credits;

import com.google.gson.annotations.SerializedName;

public class CastRemote {

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}
