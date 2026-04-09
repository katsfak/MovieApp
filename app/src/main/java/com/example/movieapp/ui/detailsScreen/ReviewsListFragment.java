package com.example.movieapp.ui.detailsScreen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;
import com.example.movieapp.databinding.AllReviewsLayoutBinding;
import com.example.movieapp.domain.models.ReviewsUi;
import com.google.android.material.color.MaterialColors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReviewsListFragment extends Fragment {

    private AllReviewsLayoutBinding binding;
    private final ReviewsAdapter reviewsAdapter = new ReviewsAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = AllReviewsLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.allReviewsRecycler.setAdapter(reviewsAdapter);

        setupBackButton(view);

        if (getArguments() != null) {
            ReviewsUi[] reviewsArray = (ReviewsUi[]) getArguments().getParcelableArray("reviews");
            if (reviewsArray != null && reviewsArray.length > 0) {
                List<ReviewsUi> reviews = Arrays.asList(reviewsArray);
                reviewsAdapter.submitList(reviews);
            }
        }
    }

    private void setupBackButton(View view) {
        binding.backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.popBackStack();
        });
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
            ReviewsUi review = items.get(position);
            holder.author.setText(review.getAuthor());
            holder.content.setText(review.getContent());
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        private class ReviewViewHolder extends RecyclerView.ViewHolder {
            TextView author;
            TextView content;

            ReviewViewHolder(View itemView, TextView author, TextView content) {
                super(itemView);
                this.author = author;
                this.content = content;
            }
        }
    }

    private int dp(int value) {
        return Math.round(value * requireContext().getResources().getDisplayMetrics().density);
    }
}
