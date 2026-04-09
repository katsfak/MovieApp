package com.example.movieapp.ui.detailsScreen;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.movieapp.R;
import com.example.movieapp.databinding.DetailsLayoutBinding;
import com.example.movieapp.domain.models.DetailsBasicUi;
import com.example.movieapp.domain.models.GenreUi;
import com.example.movieapp.domain.models.MovieDetailsWithReviewsUi;
import com.example.movieapp.domain.models.MovieUi;
import com.example.movieapp.domain.models.ReviewsUi;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DetailsScreen extends Fragment {

    private DetailsLayoutBinding binding;
    private DetailsViewModel detailsViewModel;
    private int selectedMovieId = -1;
    private boolean isFavorite;
    private String homepageUrl;
    private List<ReviewsUi> allReviews = new ArrayList<>();
    private final ReviewsAdapter reviewsAdapter = new ReviewsAdapter();
    private final SimilarMoviesAdapter similarMoviesAdapter = new SimilarMoviesAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = DetailsLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailsViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);
        int movieId = -1;
        if (getArguments() != null) {
            movieId = getArguments().getInt("MOVIE_ID", -1);
            if (movieId == -1) {
                movieId = getArguments().getInt("movieId", -1);
            }
            isFavorite = getArguments().getBoolean("IS_FAVORITE", false);
        }
        selectedMovieId = movieId;

        if (movieId == -1) {
            Snackbar.make(binding.getRoot(), "Movie id is missing", Snackbar.LENGTH_LONG).show();
            return;
        }

        setupBackButton(view);
        setupRecyclerViews();
        observeViewModel();
        setupViewAllReviewsButton();
        detailsViewModel.fetchMovieDetailsWithReviews(movieId);
        updateFavoriteIcon();
        setupFavoriteButton();
        setupShareButton();
    }

    private void setupRecyclerViews() {
        binding.reviewsRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.reviewsRecycler.setAdapter(reviewsAdapter);

        binding.similarRecycler.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.similarRecycler.setAdapter(similarMoviesAdapter);
    }

    private void setupBackButton(View view) {
        binding.backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });
    }

    private void observeViewModel() {
        detailsViewModel.movieDetailsLiveData.observe(getViewLifecycleOwner(), this::bindRealMovie);
        detailsViewModel.isLoading.observe(getViewLifecycleOwner(),
                loading -> {
                    binding.skeletonOverlay.setVisibility(loading ? View.VISIBLE : View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.backButton.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);
                    binding.posterImage.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);
                });
        detailsViewModel.error.observe(getViewLifecycleOwner(), errorMsg -> {
            if (!TextUtils.isEmpty(errorMsg)) {
                Snackbar.make(binding.getRoot(), errorMsg, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void bindRealMovie(MovieDetailsWithReviewsUi movieData) {
        if (movieData == null || movieData.getMovieDetails() == null) {
            return;
        }
        binding.skeletonOverlay.setVisibility(View.GONE);
        binding.backButton.setVisibility(View.VISIBLE);
        binding.posterImage.setVisibility(View.VISIBLE);
        DetailsBasicUi details = movieData.getMovieDetails();
        selectedMovieId = details.getId();
        String imagePath = !TextUtils.isEmpty(details.getPosterPath())
                ? details.getPosterPath()
                : details.getBackdropPath();

        Glide.with(binding.posterImage.getContext())
                .load(TextUtils.isEmpty(imagePath) ? null : "https://image.tmdb.org/t/p/w780" + imagePath)
                .apply(new RequestOptions().placeholder(R.drawable.loading))
                .into(binding.posterImage);

        binding.movieTitle.setText(details.getTitle());
        binding.genresValue.setText(formatGenres(details.getGenres()));
        binding.releaseDate
                .setText(getString(R.string.release_date_pretty_format, formatReleaseDate(details.getReleaseDate())));
        binding.runtime.setText(formatRuntime(details.getRuntime()));
        binding.starRating.setRating((float) (details.getVoteAverage() / 2.0));
        binding.ratingValue.setText("");
        binding.ratingValue.setVisibility(View.GONE);
        binding.descriptionValue.setText(details.getOverview());
        binding.castValue.setText(formatCast(movieData.getCast()));

        allReviews = movieData.getReviews() == null ? new ArrayList<>() : movieData.getReviews();
        List<ReviewsUi> reviewsSubset = allReviews.subList(0, Math.min(3, allReviews.size()));
        reviewsAdapter.submitList(reviewsSubset);
        binding.reviewsLabel.setVisibility(reviewsSubset.isEmpty() ? View.GONE : View.VISIBLE);
        binding.reviewsRecycler.setVisibility(reviewsSubset.isEmpty() ? View.GONE : View.VISIBLE);
        binding.viewAllReviewsButton.setVisibility(allReviews.size() > 3 ? View.VISIBLE : View.GONE);

        List<MovieUi> allSimilar = movieData.getSimilarMovies() == null ? new ArrayList<>()
                : movieData.getSimilarMovies();
        List<MovieUi> similarSubset = allSimilar.subList(0, Math.min(6, allSimilar.size()));
        if (similarSubset.isEmpty()) {
            binding.similarLabel.setVisibility(View.GONE);
            binding.similarRecycler.setVisibility(View.GONE);
            similarMoviesAdapter.submitList(new ArrayList<>());
        } else {
            binding.similarLabel.setVisibility(View.VISIBLE);
            binding.similarRecycler.setVisibility(View.VISIBLE);
            similarMoviesAdapter.submitList(similarSubset);
        }

        homepageUrl = details.getHomepage();
        binding.shareButton.setVisibility(TextUtils.isEmpty(homepageUrl) ? View.GONE : View.VISIBLE);
    }

    private void setupFavoriteButton() {
        binding.favoriteButton.setOnClickListener(v -> {
            if (selectedMovieId == -1) {
                return;
            }
            detailsViewModel.toggleFavorite(selectedMovieId);
            isFavorite = !isFavorite;
            updateFavoriteIcon();
        });
    }

    private void updateFavoriteIcon() {
        int icon = isFavorite ? R.drawable.ic_favorite_selected : R.drawable.ic_favorite_unselect;
        binding.favoriteButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), icon));
    }

    private void setupShareButton() {
        binding.shareButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(homepageUrl)) {
                return;
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, homepageUrl);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_movie)));
        });
    }

    private void setupViewAllReviewsButton() {
        binding.viewAllReviewsButton.setOnClickListener(v -> {
            if (allReviews.isEmpty()) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putParcelableArray("reviews", allReviews.toArray(new ReviewsUi[0]));
            Navigation.findNavController(v).navigate(R.id.action_detailsScreen_to_reviewsListFragment, bundle);
        });
    }

    private String formatRuntime(int minutes) {
        int hours = minutes / 60;
        int remaining = minutes % 60;
        return getString(R.string.runtime_compact_format, hours, remaining);
    }

    private String formatReleaseDate(String releaseDate) {
        if (TextUtils.isEmpty(releaseDate)) {
            return "";
        }
        String[] parts = releaseDate.split("-");
        if (parts.length != 3) {
            return releaseDate;
        }
        try {
            int month = Integer.parseInt(parts[1]);
            String[] months = {
                    "January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"
            };
            String monthName = (month >= 1 && month <= 12) ? months[month - 1] : parts[1];
            return String.format(Locale.US, "%s %s %s", Integer.parseInt(parts[2]), monthName, parts[0]);
        } catch (NumberFormatException ignored) {
            return releaseDate;
        }
    }

    private String formatGenres(List<GenreUi> genres) {
        if (genres == null || genres.isEmpty()) {
            return getString(R.string.genres_fallback);
        }
        List<String> names = new ArrayList<>();
        for (GenreUi genre : genres) {
            names.add(genre.getName());
        }
        return TextUtils.join(" • ", names);
    }

    private String formatCast(List<String> cast) {
        if (cast == null || cast.isEmpty()) {
            return getString(R.string.cast_unavailable);
        }
        int end = Math.min(6, cast.size());
        return TextUtils.join(", ", cast.subList(0, end));
    }

    private class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {
        private List<ReviewsUi> items = new ArrayList<>();

        void submitList(List<ReviewsUi> values) {
            items = new ArrayList<>(values);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LinearLayout container = new LinearLayout(parent.getContext());
            container.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            container.setOrientation(LinearLayout.VERTICAL);

            TextView author = new TextView(parent.getContext());
            author.setTextSize(16f);
            author.setTextColor(MaterialColors.getColor(parent, androidx.appcompat.R.attr.colorPrimary));
            author.setTypeface(author.getTypeface(), android.graphics.Typeface.BOLD);
            author.setPadding(0, dp(6), 0, dp(4));

            TextView content = new TextView(parent.getContext());
            content.setTextSize(16f);
            content.setTextColor(MaterialColors.getColor(parent, android.R.attr.textColorPrimary));
            content.setTypeface(content.getTypeface(), android.graphics.Typeface.BOLD);
            content.setPadding(0, 0, 0, dp(8));

            container.addView(author);
            container.addView(content);
            return new ReviewViewHolder(container, author, content);
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
            ReviewsUi item = items.get(position);
            holder.author.setText(getString(R.string.review_author_format, item.getAuthor()));
            holder.content.setText(item.getContent());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ReviewViewHolder extends RecyclerView.ViewHolder {
            final TextView author;
            final TextView content;

            ReviewViewHolder(@NonNull View itemView, TextView author, TextView content) {
                super(itemView);
                this.author = author;
                this.content = content;
            }
        }
    }

    private class SimilarMoviesAdapter extends RecyclerView.Adapter<SimilarMoviesAdapter.PosterViewHolder> {
        private List<MovieUi> items = new ArrayList<>();

        void submitList(List<MovieUi> values) {
            items = new ArrayList<>(values);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PosterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView poster = new ImageView(parent.getContext());
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(dp(92), dp(132));
            params.setMarginEnd(dp(12));
            poster.setLayoutParams(params);
            poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new PosterViewHolder(poster);
        }

        @Override
        public void onBindViewHolder(@NonNull PosterViewHolder holder, int position) {
            MovieUi item = items.get(position);
            holder.poster.setContentDescription(item.getTitle());
            Glide.with(holder.poster.getContext())
                    .load(TextUtils.isEmpty(item.getPosterPath()) ? null
                            : "https://image.tmdb.org/t/p/w342" + item.getPosterPath())
                    .apply(new RequestOptions().placeholder(R.drawable.loading).centerCrop())
                    .into(holder.poster);

            holder.poster.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putInt("MOVIE_ID", item.getId());
                bundle.putBoolean("IS_FAVORITE", item.isFavorite());
                Navigation.findNavController(binding.getRoot()).navigate(R.id.detailsScreen, bundle);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class PosterViewHolder extends RecyclerView.ViewHolder {
            final ImageView poster;

            PosterViewHolder(@NonNull View itemView) {
                super(itemView);
                this.poster = (ImageView) itemView;
            }
        }
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
