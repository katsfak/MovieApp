package com.example.movieapp.data.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie_db")
    LiveData<List<MovieEntity>> getAll();

    /**
     * Maybe: emits the item if found, completes empty if not found.
     * Never blocks the main thread.
     */
    @Query("SELECT * FROM movie_db WHERE id = :id LIMIT 1")
    Maybe<MovieEntity> getMovieById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MovieEntity movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MovieEntity> movies);

    @Query("SELECT * FROM movie_db WHERE is_favorite = 1")
    LiveData<List<MovieEntity>> getFavorites();

    @Delete
    void delete(MovieEntity movie);

    /**
     * Atomic SQL toggle — no race condition possible.
     * Single read+write at the DB engine level.
     */
    @Query("UPDATE movie_db SET is_favorite = CASE WHEN is_favorite = 1 THEN 0 ELSE 1 END WHERE id = :id")
    Completable toggleFavorite(int id);
}
