package com.example.movieapp.data.repo;

import com.example.movieapp.common.MovieMapper;
import com.example.movieapp.data.model.details.credits.MovieCreditsApiResponse;
import com.example.movieapp.data.model.details.reviews.MovieDetailsReviewApiResponse;
import com.example.movieapp.data.networkServices.MovieApiService;
import com.example.movieapp.domain.models.DetailsBasicUi;
import com.example.movieapp.domain.models.MovieUi;
import com.example.movieapp.domain.models.ReviewsUi;
import com.example.movieapp.domain.repoInterfaces.MovieDetailsRepository;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Single;

public class MovieDetailsRepositoryImpl implements MovieDetailsRepository {

    private MovieApiService apiService;

    public MovieDetailsRepositoryImpl(MovieApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Single<DetailsBasicUi> getMovieById(Integer movie_id) {
        return apiService.getMovieById(movie_id)
                .map(MovieMapper::mapToUiModel);
    }

    @Override
    public Single<List<ReviewsUi>> getReviewsById(Integer movie_id) {
        return apiService.getReviewsById(movie_id)
                .map(MovieDetailsReviewApiResponse::getResults)
                .map(movies -> movies.stream()
                        .map(MovieMapper::mapToUiModel)
                        .collect(Collectors.toList()));
    }

    @Override
    public Single<List<MovieUi>> getSimilarMoviesById(Integer movie_id) {
        return apiService.getSimilarMoviesById(movie_id)
                .map(response -> MovieMapper.mapToUiMovieList(response.getResults()));
    }

    @Override
    public Single<List<String>> getCastById(Integer movie_id) {
        return apiService.getCreditsById(movie_id)
                .map(MovieCreditsApiResponse::getCast)
                .map(cast -> cast.stream()
                        .map(member -> member.getName() == null ? "" : member.getName())
                        .filter(name -> !name.isEmpty())
                        .collect(Collectors.toList()));
    }
}
