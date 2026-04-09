package com.example.movieapp.ui.homeScreen;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.movieapp.data.network.NetworkUtil;
import com.example.movieapp.domain.models.MovieUi;
import com.example.movieapp.domain.usecase.GetMoviesUseCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class MovieViewModel extends AndroidViewModel {

    private enum Mode {
        ALL,
        FAVORITES
    }

    private final GetMoviesUseCase getMoviesUseCase;

    /**
     * CompositeDisposable lives here — cleared in onCleared().
     * This is the correct owner: the ViewModel knows its own lifecycle.
     */
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<MovieUi>> _movies = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<MoviesUiState> _uiState = new MutableLiveData<>();
    public final LiveData<MoviesUiState> uiState = _uiState;

    public final LiveData<List<MovieUi>> movies = _movies;
    private LiveData<List<MovieUi>> currentSource;
    private Observer<List<MovieUi>> currentSourceObserver;
    private final List<MovieUi> cachedMovies = new ArrayList<>();
    private Mode currentMode = Mode.ALL;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    @Inject
    public MovieViewModel(Application application, GetMoviesUseCase getMoviesUseCase) {
        super(application);
        this.getMoviesUseCase = getMoviesUseCase;

        if (NetworkUtil.isNetworkAvailable(application)) {
            loadAllMovies();
        } else {
            startLoading();
            showFavoritesOnly();
        }
    }

    private void showFavoritesOnly() {
        currentMode = Mode.FAVORITES;
        switchSource(getMoviesUseCase.executeFavorites());
    }

    public void showFavoritesOnlyMode() {
        startLoading();
        showFavoritesOnly();
    }

    private void showAllMovies() {
        currentMode = Mode.ALL;
        switchSource(getMoviesUseCase.execute());
    }

    public void syncCurrentModeFromLocal() {
        if (currentMode == Mode.FAVORITES) {
            switchSource(getMoviesUseCase.executeFavorites());
        } else {
            switchSource(getMoviesUseCase.execute());
        }
    }

    private void loadAllMovies() {
        startLoading();
        showAllMovies();
        disposables.add(
                getMoviesUseCase.refresh()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> _isLoading.setValue(false),
                                error -> {
                                    _isLoading.setValue(false);
                                    showFavoritesOnly();
                                    _uiState.setValue(MoviesUiState.error(
                                            "Failed to load movies: " + error.getMessage(),
                                            cachedMovies));
                                }));
    }

    private void switchSource(LiveData<List<MovieUi>> newSource) {
        if (currentSource != null) {
            if (currentSourceObserver != null) {
                currentSource.removeObserver(currentSourceObserver);
            }
        }
        currentSource = newSource;
        currentSourceObserver = movies -> {
            List<MovieUi> safeMovies = movies == null ? new ArrayList<>() : movies;
            _movies.setValue(safeMovies);
            cachedMovies.clear();
            cachedMovies.addAll(safeMovies);

            if (safeMovies.isEmpty()) {
                _uiState.setValue(MoviesUiState.empty(getEmptyMessage()));
            } else {
                _uiState.setValue(MoviesUiState.success(safeMovies));
            }
        };
        currentSource.observeForever(currentSourceObserver);
    }

    /**
     * Triggers a network refresh. The Completable from the use case
     * is subscribed here; disposable is stored and cleared in onCleared().
     */
    public void refresh() {
        showAllMovies();
        startLoading();
        disposables.add(
                getMoviesUseCase.refresh()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    _isLoading.setValue(false);
                                    if (!cachedMovies.isEmpty()) {
                                        _uiState.setValue(MoviesUiState.success(cachedMovies));
                                    }
                                },
                                error -> {
                                    _isLoading.setValue(false);
                                    showFavoritesOnly();
                                    String message = "Failed to load movies: " + error.getMessage();
                                    _error.setValue(message);
                                    _uiState.setValue(MoviesUiState.error(message, cachedMovies));
                                }));
    }

    /**
     * Atomic toggle by movie ID — delegates to SQL-level toggle.
     * Disposable is added to the same CompositeDisposable.
     */
    public void toggleFavorite(MovieUi movie) {
        disposables.add(
                getMoviesUseCase.toggleFavorite(movie.getId())
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                () -> {
                                    /* success — LiveData re-emits automatically */ },
                                err -> {
                                    String message = "Toggle failed: " + err.getMessage();
                                    _error.setValue(message);
                                    _uiState.setValue(MoviesUiState.error(message, cachedMovies));
                                }));
    }

    private void startLoading() {
        _isLoading.setValue(true);
        _uiState.setValue(MoviesUiState.loading(cachedMovies, true));
    }

    private String getEmptyMessage() {
        if (currentMode == Mode.FAVORITES) {
            return "No favorites yet.";
        }
        return "No movies found.";
    }

    @Override
    protected void onCleared() {
        if (currentSource != null && currentSourceObserver != null) {
            currentSource.removeObserver(currentSourceObserver);
        }
        disposables.clear(); // safe lifecycle cleanup
        super.onCleared();
    }
}
