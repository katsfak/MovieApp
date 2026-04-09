package com.example.movieapp.data.model.details.reviews;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDetailsReviewApiResponse {
    @SerializedName("results")
    private List<ReviewsRemote> results;

    public List<ReviewsRemote> getResults() {
        return results;
    }

}

