package com.example.movieapp.ui.homeScreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieapp.R;

public class MovieSkeletonAdapter extends RecyclerView.Adapter<MovieSkeletonAdapter.SkeletonViewHolder> {

    private final int itemCount;

    public MovieSkeletonAdapter(int itemCount) {
        this.itemCount = itemCount;
    }

    @NonNull
    @Override
    public SkeletonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie_skeleton, parent, false);
        return new SkeletonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SkeletonViewHolder holder, int position) {
        Animation pulse = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.skeleton_pulse);
        holder.itemView.startAnimation(pulse);
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    @Override
    public void onViewRecycled(@NonNull SkeletonViewHolder holder) {
        holder.itemView.clearAnimation();
        super.onViewRecycled(holder);
    }

    static class SkeletonViewHolder extends RecyclerView.ViewHolder {
        SkeletonViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
