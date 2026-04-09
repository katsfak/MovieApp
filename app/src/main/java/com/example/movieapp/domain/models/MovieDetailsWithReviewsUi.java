package com.example.movieapp.domain.models;

import java.util.List;

public class MovieDetailsWithReviewsUi {

    private final DetailsBasicUi movieDetails;
    private final List<ReviewsUi> reviews;
    private final List<String> cast;
    private final List<MovieUi> similarMovies;

    public MovieDetailsWithReviewsUi(DetailsBasicUi movieDetails,
            List<ReviewsUi> reviews,
            List<String> cast,
            List<MovieUi> similarMovies) {
        this.movieDetails = movieDetails;
        this.reviews = reviews;
        this.cast = cast;
        this.similarMovies = similarMovies;
    }

    public DetailsBasicUi getMovieDetails() {
        return movieDetails;
    }

    public List<ReviewsUi> getReviews() {
        return reviews;
    }

    public List<String> getCast() {
        return cast;
    }

    public List<MovieUi> getSimilarMovies() {
        return similarMovies;
    }
}
