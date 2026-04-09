package com.example.movieapp.data.model.details.credits;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieCreditsApiResponse {

    @SerializedName("cast")
    private List<CastRemote> cast;

    public List<CastRemote> getCast() {
        return cast;
    }
}
