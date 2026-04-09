package com.example.movieapp.ui.profile;

import android.os.Bundle;
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
import com.example.movieapp.databinding.FragmentFavoritesBinding;
import com.example.movieapp.ui.homeScreen.MovieListAdapter;
import com.example.movieapp.ui.homeScreen.MovieSkeletonAdapter;
import com.example.movieapp.ui.homeScreen.MoviesUiState;
import com.example.movieapp.ui.homeScreen.MovieViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private MovieViewModel viewModel;
    private MovieListAdapter adapter;
    private MovieSkeletonAdapter skeletonAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        binding.favoritesRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MovieListAdapter(
                movie -> viewModel.toggleFavorite(movie),
                (movieId, isFavorite) -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("MOVIE_ID", movieId);
                    bundle.putBoolean("IS_FAVORITE", isFavorite);
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_favoritesFragment_to_detailsScreen, bundle);
                },
                (movieId, movieTitle) -> {
                    Bundle bundle = new Bundle();
                    bundle.putInt("MOVIE_ID", movieId);
                    Navigation.findNavController(binding.getRoot())
                            .navigate(R.id.action_favoritesFragment_to_detailsScreen, bundle);
                });
        binding.favoritesRecycler.setAdapter(adapter);
        binding.favoritesRecycler.setItemAnimator(null);

        binding.favoritesSkeletonRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        skeletonAdapter = new MovieSkeletonAdapter(8);
        binding.favoritesSkeletonRecycler.setAdapter(skeletonAdapter);
        binding.favoritesSkeletonRecycler.setItemAnimator(null);

        binding.backButton.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        viewModel.showFavoritesOnlyMode();
        viewModel.uiState.observe(getViewLifecycleOwner(), this::renderState);
    }

    private void renderState(MoviesUiState state) {
        if (state == null) {
            return;
        }

        switch (state.getStatus()) {
            case LOADING:
                binding.favoritesSkeletonRecycler.setVisibility(View.VISIBLE);
                binding.favoritesSkeletonRecycler.bringToFront();
                binding.favoritesRecycler.setVisibility(View.GONE);
                adapter.submitList(state.getMovies());
                binding.emptyStateText.setVisibility(View.GONE);
                break;
            case SUCCESS:
                binding.favoritesSkeletonRecycler.setVisibility(View.GONE);
                binding.favoritesRecycler.setVisibility(View.VISIBLE);
                adapter.submitList(state.getMovies());
                binding.emptyStateText.setVisibility(View.GONE);
                break;
            case EMPTY:
                binding.favoritesSkeletonRecycler.setVisibility(View.GONE);
                binding.favoritesRecycler.setVisibility(View.VISIBLE);
                adapter.submitList(state.getMovies());
                binding.emptyStateText.setVisibility(View.VISIBLE);
                break;
            case ERROR:
                binding.favoritesSkeletonRecycler.setVisibility(View.GONE);
                binding.favoritesRecycler.setVisibility(View.VISIBLE);
                adapter.submitList(state.getMovies());
                if (state.getMovies().isEmpty()) {
                    binding.emptyStateText.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyStateText.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
