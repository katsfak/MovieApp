package com.example.movieapp.data.model.details.videos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieVideosApiResponse {

    @SerializedName("results")
    private List<VideoRemote> results;

    public List<VideoRemote> getResults() {
        return results;
    }
}
