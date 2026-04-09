package com.example.movieapp.data.repo;

import androidx.lifecycle.LiveData;

import com.example.movieapp.common.MovieEntityMapper;
import com.example.movieapp.data.database.MovieDao;
import com.example.movieapp.data.database.MovieEntity;
import com.example.movieapp.domain.repoInterfaces.MovieDBRepository;
import com.example.movieapp.domain.repoInterfaces.MovieRepository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public class MovieDBRepositoryImpl implements MovieDBRepository {

    private final MovieDao movieDao;
    private final MovieRepository movieRepository;

    @Inject
    public MovieDBRepositoryImpl(MovieDao movieDao, MovieRepository movieRepository) {
        this.movieDao = movieDao;
        this.movieRepository = movieRepository;
    }

    /**
     * Pure getter — no side effects, no network calls.
     * Room LiveData fires automatically whenever the DB changes.
     */
    @Override
    public LiveData<List<MovieEntity>> getMovies() {
        return movieDao.getAll();
    }

    @Override
    public LiveData<List<MovieEntity>> getFavoriteMovies() {
        return movieDao.getFavorites();
    }

    /**
     * Network fetch → preserve favorites → write to Room.
     * Returns Completable; caller (ViewModel) owns the disposable and
     * clears it in onCleared().
     *
     * Uses filter+map+flatMapCompletable for clarity:
     * filter → skip null/empty response
     * map → Remote list → Entity list
     * flatMap → write to DB atomically
     */
    @Override
    public Completable refreshMovies() {
        return movieRepository.getPopularMovies()
                .subscribeOn(Schedulers.io())
                .filter(remotes -> remotes != null && !remotes.isEmpty())
                .map(MovieEntityMapper::mapToEntityMovieList)
                .flatMapCompletable(entities -> Completable.fromAction(() -> {
                    for (MovieEntity movie : entities) {
                        // Preserve favorite flag: check existing row synchronously
                        // (safe here — we are already on Schedulers.io())
                        MovieEntity existing = movieDao.getMovieById(movie.getId()).blockingGet();
                        if (existing != null && existing.isFavorite()) {
                            movie.setFavorite(true);
                        }
                        movieDao.insert(movie);
                    }
                }));
    }

    /** Completable wraps the Room insert — no raw new Thread(). */
    @Override
    public Completable insertMovie(MovieEntity movie) {
        return Completable.fromAction(() -> movieDao.insert(movie))
                .subscribeOn(Schedulers.io());
    }

    /** Completable wraps the Room delete — no raw new Thread(). */
    @Override
    public Completable deleteMovie(MovieEntity movie) {
        return Completable.fromAction(() -> movieDao.delete(movie))
                .subscribeOn(Schedulers.io());
    }

    /**
     * Delegates to the atomic SQL toggle in the DAO.
     * No race condition — single DB operation at the SQL level.
     */
    @Override
    public Completable toggleFavorite(int movieId) {
        return movieDao.toggleFavorite(movieId)
                .subscribeOn(Schedulers.io());
    }
}
