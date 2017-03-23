package com.apogiatzis.udacitymovieproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apogiatzis.udacitymovieproject.models.MovieReview;

import java.util.ArrayList;

/**
 * Created by andre on 17/03/2017.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewAdapterViewHolder> {

    ArrayList<MovieReview> mReviewData;

    @Override
    public MovieReviewAdapter.MovieReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieReviewAdapter.MovieReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewAdapter.MovieReviewAdapterViewHolder holder, int position) {
        MovieReview review = mReviewData.get(position);

        holder.mMovieReviewAuthorTextView.setText(review.getAuthor());
        holder.mMovieReviewContentTextView.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (mReviewData == null) return 0;
        return mReviewData.size();
    }

    public void setReviewData(ArrayList<MovieReview> reviewData) {
        mReviewData = reviewData;
        notifyDataSetChanged();
    }

    public class MovieReviewAdapterViewHolder extends RecyclerView.ViewHolder{

        private TextView mMovieReviewAuthorTextView;
        private TextView mMovieReviewContentTextView;

        public MovieReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mMovieReviewAuthorTextView = (TextView) itemView.findViewById(R.id.tv_movie_review_author);
            mMovieReviewContentTextView = (TextView) itemView.findViewById(R.id.tv_movie_review_content);
        }
    }
}
