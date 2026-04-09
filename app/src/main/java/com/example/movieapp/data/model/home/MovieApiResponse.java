package com.example.movieapp.data.model.home;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieApiResponse {
    @SerializedName("results")
    private List<MovieRemote> results;

    public List<MovieRemote> getResults() {
        return results;
    }

}

