package com.example.movieapp.common;

import com.example.movieapp.data.database.MovieEntity;
import com.example.movieapp.data.model.home.MovieRemote;
import com.example.movieapp.domain.models.MovieUi;

import java.util.List;
import java.util.stream.Collectors;

public class MovieEntityMapper {

    public static MovieEntity mapToMovieEntity(MovieUi movie) {
        return new MovieEntity(
                movie.getId(),
                movie.getTitle(),
                movie.getOverview(),
                movie.getPosterPath(),
                movie.getVoteAverage(),
                movie.isFavorite(),
                movie.getBackdrop_path(),
                movie.getReleaseDate());
    }

    public static MovieUi mapToMovieUi(MovieEntity movie) {
        return new MovieUi(
                movie.getId(),
                movie.getTitle(),
                movie.getOverview(),
                movie.getPosterPath(),
                movie.getVoteAverage(),
                movie.isFavorite(),
                movie.getBackdropPath(),
                movie.getReleaseDate());
    }

    public static MovieEntity mapToMovieEntityFromRemote(MovieRemote movieRemote) {
        return new MovieEntity(
                movieRemote.getId(),
                movieRemote.getTitle(),
                movieRemote.getOverview(),
                movieRemote.getPosterPath(),
                movieRemote.getVoteAverage(),
                movieRemote.isFavorite(),
                movieRemote.getBackdropPath(),
                movieRemote.getReleaseDate());
    }

    public static List<MovieUi> mapToUiMovieList(List<MovieEntity> movieEntities) {
        return movieEntities.stream()
                .map(MovieEntityMapper::mapToMovieUi)
                .collect(Collectors.toList());
    }

    public static List<MovieEntity> mapToEntityMovieList(List<MovieRemote> movieRemotes) {
        return movieRemotes.stream()
                .map(MovieEntityMapper::mapToMovieEntityFromRemote)
                .collect(Collectors.toList());
    }
}
