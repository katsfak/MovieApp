package com.example.movieapp.domain.models;

import java.util.List;

public class DetailsBasicUi {

    private String backdropPath;
    private List<GenreUi> genres;
    private int id;
    private String overview;
    private String posterPath; // Assuming posterPath should be a String for UI purposes
    private String releaseDate;
    private String title;
    private double voteAverage;
    private String homepage;
    private int runtime;

    public DetailsBasicUi(String backdropPath, List<GenreUi> genres, int id, String overview, String posterPath, String releaseDate, String title, double voteAverage, String homepage, int runtime) {
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

    public List<GenreUi> getGenres() {
        return genres;
    }

    public int getId() {
        return id;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
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

    public void setGenres(List<GenreUi> genres) {
        this.genres = genres;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPosterPath(String posterPath) {
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

    @Override
    public String toString() {
        return "DetailsBasicUi{" +
                "backdropPath='" + backdropPath + '\'' +
                ", genres=" + genres +
                ", id=" + id +
                ", overview='" + overview + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", title='" + title + '\'' +
                ", voteAverage=" + voteAverage +
                ", homepage='" + homepage + '\'' +
                ", runtime=" + runtime +
                '}';
    }
}

