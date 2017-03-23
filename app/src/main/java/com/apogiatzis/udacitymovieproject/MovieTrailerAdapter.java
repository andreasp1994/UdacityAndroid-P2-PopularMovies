package com.apogiatzis.udacitymovieproject;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apogiatzis.udacitymovieproject.models.MovieTrailer;
import com.apogiatzis.udacitymovieproject.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by andre on 16/03/2017.
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerAdapterViewHolder> {

    private ArrayList<MovieTrailer> mTrailerData;
    private MovieTrailerAdapterOnClickListener mListener;

    public MovieTrailerAdapter(MovieTrailerAdapterOnClickListener listener){
        this.mListener = listener;
    }

    @Override
    public MovieTrailerAdapter.MovieTrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new MovieTrailerAdapter.MovieTrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailerAdapter.MovieTrailerAdapterViewHolder holder, int position) {
        MovieTrailer trailer = mTrailerData.get(position);

        holder.mMovieTrailerTitle.setText(trailer.getName());
        holder.mMovieTrailerThumbImageView.setImageDrawable(null);
        String posterUrl = NetworkUtils.buildMovieTrailerThumbUrl(trailer.getKey()).toString();
        Picasso.with(holder.mMovieTrailerThumbImageView.getContext()).load(posterUrl).into(holder.mMovieTrailerThumbImageView);
    }

    @Override
    public int getItemCount() {
        if (mTrailerData == null) return 0;
        return mTrailerData.size();
    }

    public void setTrailerData(ArrayList<MovieTrailer> trailerData) {
        mTrailerData = trailerData;
        notifyDataSetChanged();
    }

    public class MovieTrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mMovieTrailerThumbImageView;
        private TextView mMovieTrailerTitle;

        public MovieTrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mMovieTrailerThumbImageView = (ImageView) itemView.findViewById(R.id.iv_movie_trailer_thumbnail);
            mMovieTrailerTitle = (TextView) itemView.findViewById(R.id.tv_movie_trailer_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mListener.onTrailerClick(mTrailerData.get(adapterPosition));
        }
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieTrailerAdapterOnClickListener {
        void onTrailerClick(MovieTrailer trailer);
    }
}
