package com.apogiatzis.udacitymovieproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apogiatzis.udacitymovieproject.models.Movie;
import com.apogiatzis.udacitymovieproject.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by andre on 16/01/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private ArrayList<Movie> mMovieData;

    private MovieAdapterOnClickListener mClickListener;

    public MovieAdapter(MovieAdapterOnClickListener listener){
        this.mClickListener = listener;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = mMovieData.get(position);

        //Get movie poster and display it.
        holder.mMoviePosterImageView.setImageDrawable(null);
        String posterUrl = NetworkUtils.buildMoviePosterUrl(movie.getPosterPath(), Movie.MOVIE_POSTER_DEFAULT_SIZE).toString();
        Picasso.with(holder.mMoviePosterImageView.getContext()).load(posterUrl).into(holder.mMoviePosterImageView);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null) return 0;
        return mMovieData.size();
    }

    public void setMovieData(ArrayList<Movie> movieData) {
        mMovieData = movieData;
        notifyDataSetChanged();
    }

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mMoviePosterImageView;

        public MovieAdapterViewHolder(View itemView) {
            super(itemView);
            mMoviePosterImageView = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickListener.onClick(mMovieData.get(adapterPosition));
        }
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickListener {
        void onClick(Movie movie);
    }

}
