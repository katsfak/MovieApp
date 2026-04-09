package com.example.movieapp.common;

import com.example.movieapp.data.model.details.basicDetails.GenreRemote;
import com.example.movieapp.data.model.details.basicDetails.MovieDetailsRemote;
import com.example.movieapp.data.model.details.reviews.ReviewsRemote;
import com.example.movieapp.data.model.home.MovieRemote;
import com.example.movieapp.domain.models.DetailsBasicUi;
import com.example.movieapp.domain.models.GenreUi;
import com.example.movieapp.domain.models.MovieUi;
import com.example.movieapp.domain.models.ReviewsUi;

import java.util.ArrayList;
import java.util.List;

public class MovieMapper {
    public static MovieUi mapToUiMovie(MovieRemote movie) {
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

    public static List<MovieUi> mapToUiMovieList(List<MovieRemote> movies) {
        List<MovieUi> uiMovies = new ArrayList<>();
        for (MovieRemote movie : movies) {
            uiMovies.add(mapToUiMovie(movie));
        }
        return uiMovies;
    }

    public static ReviewsUi mapToUiModel(ReviewsRemote remote) {
        return new ReviewsUi(
                remote.getAuthor(),
                remote.getContent(),
                remote.getCreatedAt(),
                remote.getId(),
                remote.getUpdatedAt(),
                remote.getUrl());
    }

    public static GenreUi mapToUiModel(GenreRemote remote) {
        return new GenreUi(
                remote.getId(),
                remote.getName());
    }

    public static DetailsBasicUi mapToUiModel(MovieDetailsRemote remote) {
        List<GenreUi> genres = new ArrayList<>();

        for (GenreRemote genre : remote.getGenres()) {
            genres.add(MovieMapper.mapToUiModel(genre));
        }

        return new DetailsBasicUi(
                remote.getBackdropPath(),
                genres,
                remote.getId(),
                remote.getOverview(),
                remote.getPosterPath() != null ? remote.getPosterPath().toString() : null, // Assuming you convert
                                                                                           // Object to String
                remote.getReleaseDate(),
                remote.getTitle(),
                remote.getVoteAverage(),
                remote.getHomepage(),
                remote.getRuntime());
    }
}
