package com.example.movieapp.ui.homeScreen;

import com.example.movieapp.domain.models.MovieUi;

import java.util.ArrayList;
import java.util.List;

public class MoviesUiState {

    public enum Status {
        LOADING,
        SUCCESS,
        EMPTY,
        ERROR
    }

    private final Status status;
    private final List<MovieUi> movies;
    private final String message;
    private final boolean showSkeleton;

    private MoviesUiState(Status status, List<MovieUi> movies, String message, boolean showSkeleton) {
        this.status = status;
        this.movies = movies == null ? new ArrayList<>() : new ArrayList<>(movies);
        this.message = message;
        this.showSkeleton = showSkeleton;
    }

    public static MoviesUiState loading(List<MovieUi> currentMovies, boolean showSkeleton) {
        return new MoviesUiState(Status.LOADING, currentMovies, null, showSkeleton);
    }

    public static MoviesUiState success(List<MovieUi> movies) {
        return new MoviesUiState(Status.SUCCESS, movies, null, false);
    }

    public static MoviesUiState empty(String message) {
        return new MoviesUiState(Status.EMPTY, new ArrayList<>(), message, false);
    }

    public static MoviesUiState error(String message, List<MovieUi> currentMovies) {
        return new MoviesUiState(Status.ERROR, currentMovies, message, false);
    }

    public Status getStatus() {
        return status;
    }

    public List<MovieUi> getMovies() {
        return new ArrayList<>(movies);
    }

    public String getMessage() {
        return message;
    }

    public boolean isShowSkeleton() {
        return showSkeleton;
    }
}
