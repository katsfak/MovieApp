package com.example.movieapp.domain.repoInterfaces;

import androidx.lifecycle.LiveData;

import com.example.movieapp.data.database.MovieEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

public interface MovieDBRepository {

    /** Single source of truth — Room LiveData, auto-updates on DB change. */
    LiveData<List<MovieEntity>> getMovies();

    /** Favorites-only stream used for offline-first launch mode. */
    LiveData<List<MovieEntity>> getFavoriteMovies();

    /**
     * Fetch from network and write to Room.
     * Returns Completable so the ViewModel can react to success/error
     * and manage the disposable in onCleared().
     */
    Completable refreshMovies();

    /** All write operations return Completable — no raw threads. */
    Completable insertMovie(MovieEntity movie);

    Completable deleteMovie(MovieEntity movie);

    /**
     * Atomic SQL toggle — safe against double-tap race conditions.
     * Passes just the ID; no need to pass the full entity.
     */
    Completable toggleFavorite(int movieId);
}
