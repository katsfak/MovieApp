package com.example.movieapp.ui.detailsScreen;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.AndroidViewModel;

import com.example.movieapp.data.network.NetworkUtil;
import com.example.movieapp.domain.models.DetailsBasicUi;
import com.example.movieapp.domain.models.MovieDetailsWithReviewsUi;
import com.example.movieapp.domain.models.MovieUi;
import com.example.movieapp.domain.usecase.DetailsUseCase;
import com.example.movieapp.domain.usecase.GetMoviesUseCase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class DetailsViewModel extends AndroidViewModel {

    private static final long RETRY_DELAY_MS = 2000L;

    private final DetailsUseCase detailsUseCase;
    private final GetMoviesUseCase getMoviesUseCase;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final Handler retryHandler = new Handler(Looper.getMainLooper());

    private final MutableLiveData<MovieDetailsWithReviewsUi> _movieDetails = new MutableLiveData<>();
    public final LiveData<MovieDetailsWithReviewsUi> movieDetailsLiveData = _movieDetails;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    private int pendingMovieId = -1;
    private final Runnable retryRunnable = new Runnable() {
        @Override
        public void run() {
            if (pendingMovieId == -1) {
                return;
            }

            if (NetworkUtil.isNetworkAvailable(getApplication())) {
                performFetch(pendingMovieId);
            } else {
                retryHandler.postDelayed(this, RETRY_DELAY_MS);
            }
        }
    };

    @Inject
    public DetailsViewModel(Application application, DetailsUseCase detailsUseCase, GetMoviesUseCase getMoviesUseCase) {
        super(application);
        this.detailsUseCase = detailsUseCase;
        this.getMoviesUseCase = getMoviesUseCase;
    }

    public void fetchMovieDetailsWithReviews(int movieId) {
        pendingMovieId = movieId;
        cancelRetryLoop();
        _isLoading.setValue(true);
        if (!NetworkUtil.isNetworkAvailable(getApplication())) {
            startRetryLoop();
            return;
        }

        performFetch(movieId);
    }

    private void performFetch(int movieId) {
        disposables.add(
                detailsUseCase.execute(movieId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                details -> {
                                    _isLoading.setValue(false);
                                    cancelRetryLoop();
                                    cacheMovieForFavorites(details.getMovieDetails());
                                    _movieDetails.setValue(details);
                                },
                                throwable -> {
                                    if (shouldRetryWhenOffline(throwable)) {
                                        _isLoading.setValue(true);
                                        startRetryLoop();
                                    } else {
                                        _isLoading.setValue(false);
                                        _error.setValue("Error loading details: " + throwable.getMessage());
                                        Log.e("DetailsViewModel", "fetchMovieDetailsWithReviews", throwable);
                                    }
                                }));
    }

    private void startRetryLoop() {
        retryHandler.removeCallbacks(retryRunnable);
        retryHandler.postDelayed(retryRunnable, RETRY_DELAY_MS);
    }

    private void cancelRetryLoop() {
        retryHandler.removeCallbacks(retryRunnable);
    }

    private boolean shouldRetryWhenOffline(Throwable throwable) {
        return !NetworkUtil.isNetworkAvailable(getApplication())
                || throwable instanceof java.io.IOException
                || throwable instanceof java.net.UnknownHostException;
    }

    public void toggleFavorite(int movieId) {
        // Keep this write independent from the Details screen lifecycle so back
        // navigation
        // does not cancel the DB toggle before Home receives the updated favorite
        // state.
        getMoviesUseCase.toggleFavorite(movieId)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        () -> {
                        },
                        throwable -> {
                            _error.postValue("Error updating favorite: " + throwable.getMessage());
                            Log.e("DetailsViewModel", "toggleFavorite", throwable);
                        });
    }

    private void cacheMovieForFavorites(DetailsBasicUi details) {
        if (details == null) {
            return;
        }

        MovieUi movieUi = new MovieUi(
                details.getId(),
                details.getTitle(),
                details.getOverview(),
                details.getPosterPath(),
                details.getVoteAverage(),
                false,
                details.getBackdropPath(),
                details.getReleaseDate());

        disposables.add(
                getMoviesUseCase.upsertMovie(movieUi)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                () -> {
                                },
                                throwable -> Log.w("DetailsViewModel", "cacheMovieForFavorites", throwable)));
    }

    @Override
    protected void onCleared() {
        cancelRetryLoop();
        disposables.clear();
        super.onCleared();
    }
}
