package com.example.movieapp.data.model.details.basicDetails;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieDetailsRemote {
    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("genres")
    private List<GenreRemote> genres;

    @SerializedName("id")
    private int id;

    @SerializedName("overview")
    private String overview;

    @SerializedName("poster_path")
    private Object posterPath;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("title")
    private String title;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("homepage")
    private String homepage;

    @SerializedName("runtime")
    private int runtime;

    public MovieDetailsRemote(String backdropPath, List<GenreRemote> genres, int id, String overview, Object posterPath, String releaseDate, String title, double voteAverage, String homepage, int runtime) {
        this.backdropPath = backdropPath;
        this.genres = genres;
        this.id = id;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.title = title;
        this.voteAverage = voteAverage;
        this.homepage = homepage;
        this.runtime = runtime;
    }

    // Getters
    public String getBackdropPath() {
        return backdropPath;
    }

    public List<GenreRemote> getGenres() {
        return genres;
    }

    public int getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }

    public Object getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getHomepage() {
        return homepage;
    }

    public int getRuntime() {
        return runtime;
    }

    // Setters
    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setGenres(List<GenreRemote> genres) {
        this.genres = genres;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPosterPath(Object posterPath) {
        this.posterPath = posterPath;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }
}
