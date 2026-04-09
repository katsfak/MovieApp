package com.example.movieapp.domain.usecase;

import com.example.movieapp.domain.models.DetailsBasicUi;
import com.example.movieapp.domain.models.MovieDetailsWithReviewsUi;
import com.example.movieapp.domain.models.MovieUi;
import com.example.movieapp.domain.models.ReviewsUi;
import com.example.movieapp.domain.repoInterfaces.MovieDetailsRepository;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import io.reactivex.rxjava3.core.Single;

public class DetailsUseCase {

    private MovieDetailsRepository movieDetailsRepository;

    @Inject
    public DetailsUseCase(MovieDetailsRepository movieDetailsRepository) {
        this.movieDetailsRepository = movieDetailsRepository;
    }

    public Single<MovieDetailsWithReviewsUi> execute(Integer movieId) {
        Single<DetailsBasicUi> movieDetailsObservable = movieDetailsRepository.getMovieById(movieId);
        Single<List<ReviewsUi>> reviewsObservable = movieDetailsRepository.getReviewsById(movieId)
                .onErrorReturnItem(Collections.emptyList());
        Single<List<String>> castObservable = movieDetailsRepository.getCastById(movieId)
                .onErrorReturnItem(Collections.emptyList());
        Single<List<MovieUi>> similarObservable = movieDetailsRepository.getSimilarMoviesById(movieId)
                .onErrorReturnItem(Collections.emptyList());

        return Single.zip(
                movieDetailsObservable,
                reviewsObservable,
                castObservable,
                similarObservable,
                (movieDetails, reviews, cast, similarMovies) -> new MovieDetailsWithReviewsUi(movieDetails, reviews,
                        cast, similarMovies));
    }

}
