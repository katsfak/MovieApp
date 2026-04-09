package com.example.movieapp.domain.repoInterfaces;

import androidx.lifecycle.LiveData;

import com.example.movieapp.data.database.MovieEntity;
import com.example.movieapp.data.model.home.MovieRemote;

import java.util.List;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface MovieRepository {

    Single<List<MovieRemote>> getPopularMovies();
}

