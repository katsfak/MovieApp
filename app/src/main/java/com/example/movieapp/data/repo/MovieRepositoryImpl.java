package com.example.movieapp.data.repo;

import com.example.movieapp.data.model.home.MovieApiResponse;
import com.example.movieapp.data.model.home.MovieRemote;
import com.example.movieapp.data.networkServices.MovieApiService;
import com.example.movieapp.domain.repoInterfaces.MovieRepository;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class MovieRepositoryImpl implements MovieRepository {

    private final MovieApiService apiService;

    @Inject
    public MovieRepositoryImpl(MovieApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Single<List<MovieRemote>> getPopularMovies() {
        return apiService.getPopularMovies()
                .map(MovieApiResponse::getResults);
    }
}