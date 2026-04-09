package com.example.movieapp.ui.homeScreen;

import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.movieapp.R;
import com.example.movieapp.data.model.details.videos.VideoRemote;
import com.example.movieapp.data.network.NetworkUtil;
import com.example.movieapp.data.networkServices.MovieApiService;
import com.example.movieapp.databinding.FragmentMovieBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@AndroidEntryPoint
public class MovieListFragment extends Fragment {

    @Inject
    MovieApiService movieApiService;

    private MovieViewModel viewModel;
    private MovieListAdapter adapter;
    private MovieSkeletonAdapter skeletonAdapter;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean lastKnownOnline = true;
    private boolean pendingAutoRefresh = false;

    private FragmentMovieBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMovieBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MovieListAdapter(
                movie -> viewModel.toggleFavorite(movie),
                (movieId, isfavorite) -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("MOVIE_ID", movieId);
                    bundle.putBoolean("IS_FAVORITE", isfavorite);
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_movieListFragment_to_detailsScreen, bundle);
                },
                (movieId, movieTitle) -> {
                    fetchAndPlayTrailer(movieId, movieTitle);
                });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setItemAnimator(null);

        binding.skeletonRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        skeletonAdapter = new MovieSkeletonAdapter(8);
        binding.skeletonRecycler.setAdapter(skeletonAdapter);
        binding.skeletonRecycler.setItemAnimator(null);

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.refresh());
        binding.profileButton.setOnClickListener(v -> Navigation.findNavController(binding.getRoot())
                .navigate(R.id.action_movieListFragment_to_profileFragment));

        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        viewModel.uiState.observe(getViewLifecycleOwner(), this::renderState);

        connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        lastKnownOnline = NetworkUtil.isNetworkAvailable(requireContext());
        setupConnectivityCallback();
    }

    private void setupConnectivityCallback() {
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                handleConnectivityChange(true);
            }

            @Override
            public void onLost(@NonNull Network network) {
                Context context = getContext();
                boolean stillOnline = context != null && NetworkUtil.isNetworkAvailable(context);
                handleConnectivityChange(stillOnline);
            }

            @Override
            public void onUnavailable() {
                handleConnectivityChange(false);
            }
        };
    }

    private void handleConnectivityChange(boolean isOnline) {
        if (!isAdded()) {
            return;
        }
        requireActivity().runOnUiThread(() -> {
            boolean wasOffline = !lastKnownOnline;
            lastKnownOnline = isOnline;
            if (!isOnline) {
                pendingAutoRefresh = true;
                return;
            }
            if ((wasOffline || pendingAutoRefresh) && viewModel != null) {
                pendingAutoRefresh = false;
                if (binding != null) {
                    Snackbar.make(binding.getRoot(),
                            getString(R.string.internet_restored_updating_movies),
                            Snackbar.LENGTH_SHORT)
                            .show();
                }
                viewModel.refresh();
            }
        });
    }

    private void renderState(MoviesUiState state) {
        if (state == null) {
            return;
        }

        switch (state.getStatus()) {
            case LOADING:
                adapter.submitList(state.getMovies());
                binding.skeletonRecycler.setVisibility(state.isShowSkeleton() ? View.VISIBLE : View.GONE);
                binding.skeletonRecycler.bringToFront();
                binding.swipeRefresh.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                break;
            case SUCCESS:
                adapter.submitList(state.getMovies());
                binding.skeletonRecycler.setVisibility(View.GONE);
                binding.swipeRefresh.setVisibility(View.VISIBLE);
                binding.swipeRefresh.setRefreshing(false);
                break;
            case EMPTY:
                adapter.submitList(state.getMovies());
                binding.skeletonRecycler.setVisibility(View.GONE);
                binding.swipeRefresh.setVisibility(View.VISIBLE);
                binding.swipeRefresh.setRefreshing(false);
                break;
            case ERROR:
                adapter.submitList(state.getMovies());
                binding.skeletonRecycler.setVisibility(View.GONE);
                binding.swipeRefresh.setVisibility(View.VISIBLE);
                binding.swipeRefresh.setRefreshing(false);
                if (state.getMessage() != null && !state.getMessage().isEmpty()) {
                    Snackbar.make(binding.getRoot(), state.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Retry", v -> viewModel.refresh())
                            .show();
                }
                break;
        }
    }

    private void fetchAndPlayTrailer(int movieId, String movieTitle) {
        disposables.add(
                movieApiService.getVideosById(movieId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    String trailerUrl = pickYoutubeTrailerUrl(response.getResults());
                                    if (trailerUrl.isEmpty()) {
                                        String title = TextUtils.isEmpty(movieTitle)
                                                ? getString(R.string.trailer_unavailable)
                                                : movieTitle + ": " + getString(R.string.trailer_unavailable);
                                        Snackbar.make(binding.getRoot(), title, Snackbar.LENGTH_LONG).show();
                                        return;
                                    }
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl)));
                                },
                                throwable -> Snackbar.make(binding.getRoot(),
                                        getString(R.string.trailer_error),
                                        Snackbar.LENGTH_LONG).show()));
    }

    private String pickYoutubeTrailerUrl(List<VideoRemote> videos) {
        if (videos == null || videos.isEmpty()) {
            return "";
        }
        for (VideoRemote video : videos) {
            if (isPreferredTrailer(video, true)) {
                return "https://www.youtube.com/watch?v=" + video.getKey();
            }
        }
        for (VideoRemote video : videos) {
            if (isPreferredTrailer(video, false)) {
                return "https://www.youtube.com/watch?v=" + video.getKey();
            }
        }
        return "";
    }

    private boolean isPreferredTrailer(VideoRemote video, boolean officialOnly) {
        if (video == null || TextUtils.isEmpty(video.getKey())) {
            return false;
        }
        if (!"youtube".equalsIgnoreCase(video.getSite())) {
            return false;
        }
        if (officialOnly && !video.isOfficial()) {
            return false;
        }
        String type = video.getType() == null ? "" : video.getType().toLowerCase(Locale.US);
        return type.contains("trailer") || type.contains("teaser");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (connectivityManager == null || networkCallback == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                NetworkRequest request = new NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build();
                connectivityManager.registerNetworkCallback(request, networkCallback);
            }
        } catch (Exception ignored) {
            // Avoid crashing if callback registration fails on some OEM devices.
        }

        Context context = getContext();
        if (context != null) {
            handleConnectivityChange(NetworkUtil.isNetworkAvailable(context));
        }
    }

    @Override
    public void onStop() {
        if (connectivityManager != null && networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception ignored) {
                // Callback may already be unregistered; ignore safely.
            }
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.syncCurrentModeFromLocal();
        }
        Context context = getContext();
        if (context != null) {
            handleConnectivityChange(NetworkUtil.isNetworkAvailable(context));
        }
    }

}
