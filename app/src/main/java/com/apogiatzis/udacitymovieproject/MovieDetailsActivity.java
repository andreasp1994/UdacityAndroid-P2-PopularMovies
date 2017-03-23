package com.apogiatzis.udacitymovieproject;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.content.ContentValues;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apogiatzis.udacitymovieproject.models.Movie;
import com.apogiatzis.udacitymovieproject.models.MovieReview;
import com.apogiatzis.udacitymovieproject.models.MovieTrailer;
import com.apogiatzis.udacitymovieproject.persistence.FavouriteMovieContract;
import com.apogiatzis.udacitymovieproject.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity implements
        MovieTrailerAdapter.MovieTrailerAdapterOnClickListener{

    private static final String MOVIE_TRAILER_QUERY_EXTRA = "trailer_query";
    private static final String MOVIE_REVIEW_QUERY_EXTRA = "review_query";

    private static final int MOVIE_TRAILER_LOADER = 17;
    private static final int MOVIE_REVIEW_LOADER = 18;

    private TextView mMovieTitleTextView;
    private TextView mMovieReleaseDateTextView;
    private TextView mMovieRatingTextView;
    private TextView mMovieOverviewTextView;
    private ImageView mMoviePosterImageView;
    private ImageView mMovieFavouriteImageView;
    private RecyclerView mMovieTrailerRecyclerView;
    private RecyclerView mMovieReviewRecyclerView;

    private Movie movie;
    private boolean isMovieFavourite;

    private ArrayList<MovieTrailer> mTrailerList;
    private MovieTrailerAdapter mMovieTrailerAdapter;

    private ArrayList<MovieReview> mReviewList;
    private MovieReviewAdapter mMovieReviewAdapter;

    private LoaderManager.LoaderCallbacks<ArrayList<MovieTrailer>> trailerLoader = new LoaderManager.LoaderCallbacks<ArrayList<MovieTrailer>>() {

        private ArrayList<MovieTrailer> mTrailerData = null;

        @Override
        public Loader<ArrayList<MovieTrailer>> onCreateLoader(int id, final Bundle bundle) {
            return new AsyncTaskLoader<ArrayList<MovieTrailer>>(MovieDetailsActivity.this) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (mTrailerData != null) deliverResult(mTrailerData);
                    else forceLoad();
                }

                @Override
                public ArrayList<MovieTrailer> loadInBackground() {
                    ArrayList<MovieTrailer> movieTrailers = null;
                    String movieId = bundle.getString(MOVIE_TRAILER_QUERY_EXTRA);
                    if(movieId == null || TextUtils.isEmpty(movieId)) return null;
                    try{
                        movieTrailers = NetworkUtils.fetchMovieTrailers(movieId);
                    } catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                    return movieTrailers;
                }

                public void deliverResult(ArrayList<MovieTrailer> data) {
                    mTrailerData = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<MovieTrailer>> loader, ArrayList<MovieTrailer> data) {
            mMovieTrailerAdapter.setTrailerData(data);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<MovieTrailer>> loader) {
            mMovieTrailerAdapter.setTrailerData(null);
        }
    };

    private LoaderManager.LoaderCallbacks<ArrayList<MovieReview>> reviewLoader = new LoaderManager.LoaderCallbacks<ArrayList<MovieReview>>() {

        private ArrayList<MovieReview> mReviewData = null;

        @Override
        public Loader<ArrayList<MovieReview>> onCreateLoader(int id, final Bundle bundle) {
            return new AsyncTaskLoader<ArrayList<MovieReview>>(MovieDetailsActivity.this) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if(mReviewData != null) deliverResult(mReviewData);
                    else forceLoad();
                }

                @Override
                public ArrayList<MovieReview> loadInBackground() {
                    ArrayList<MovieReview> movieReviews = null;
                    String movieId = bundle.getString(MOVIE_REVIEW_QUERY_EXTRA);
                    if(movieId == null || TextUtils.isEmpty(movieId)) return null;
                    try{
                        movieReviews = NetworkUtils.fetchMovieReviews(movieId);
                    } catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                    return movieReviews;
                }

                @Override
                public void deliverResult(ArrayList<MovieReview> data) {
                    mReviewData = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<MovieReview>> loader, ArrayList<MovieReview> data) {
            mMovieReviewAdapter.setReviewData(data);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<MovieReview>> loader) {
            mMovieReviewAdapter.setReviewData(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        movie = getIntent().getExtras().getParcelable(Movie.INTENT_EXTRA_MOVIE_OBJECT);

        mMovieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mMoviePosterImageView = (ImageView) findViewById(R.id.iv_movie_details_poster);
        mMovieReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_details_release_date);
        mMovieRatingTextView = (TextView) findViewById(R.id.tv_movie_details_rating);
        mMovieOverviewTextView = (TextView) findViewById(R.id.tv_movie_details_overview);
        mMovieFavouriteImageView = (ImageView) findViewById(R.id.iv_movie_details_favourite);

        mMovieTitleTextView.setText(movie.getTitle());
        mMovieReleaseDateTextView.setText(movie.getReleaseDate());
        mMovieRatingTextView.setText(String.valueOf(movie.getVoteAverage()));
        mMovieOverviewTextView.setText(movie.getOverview());

        String posterUrl = NetworkUtils.buildMoviePosterUrl(movie.getPosterPath(), Movie.MOVIE_POSTER_DEFAULT_SIZE).toString();
        Picasso.with(mMovieTitleTextView.getContext()).load(posterUrl).into(mMoviePosterImageView);

        mTrailerList = new ArrayList<>();
        mMovieTrailerRecyclerView = (RecyclerView) findViewById(R.id.rv_trailer_container);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mMovieTrailerRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMovieTrailerRecyclerView.setHasFixedSize(true);

        mMovieTrailerAdapter = new MovieTrailerAdapter(this);
        mMovieTrailerRecyclerView.setAdapter(mMovieTrailerAdapter);

        mReviewList = new ArrayList<>();
        mMovieReviewRecyclerView = (RecyclerView) findViewById(R.id.rv_review_container);
        mMovieReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mMovieReviewAdapter = new MovieReviewAdapter();
        mMovieReviewRecyclerView.setAdapter(mMovieReviewAdapter);

        checkIfMovieIsFavourite();
        loadTrailers();
        loadReviews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIE_TRAILER_QUERY_EXTRA, String.valueOf(movie.getId()));
        queryBundle.putString(MOVIE_REVIEW_QUERY_EXTRA, String.valueOf(movie.getId()));
        getSupportLoaderManager().restartLoader(MOVIE_TRAILER_LOADER, queryBundle, trailerLoader);
        getSupportLoaderManager().restartLoader(MOVIE_REVIEW_LOADER, queryBundle, reviewLoader);
    }

    private void checkIfMovieIsFavourite(){
        Uri movieUri = FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.getId())).build();
        Cursor cur = getContentResolver().query(movieUri ,
                null,
                null,
                null,
                null);

        if (cur.moveToNext()) {
            isMovieFavourite = true;
            mMovieFavouriteImageView.setImageResource(R.drawable.ic_star_filled);
        }
        cur.close();
    }

    public void loadTrailers(){
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIE_TRAILER_QUERY_EXTRA, String.valueOf(movie.getId()));
        getSupportLoaderManager().initLoader(MOVIE_TRAILER_LOADER, queryBundle, trailerLoader);
    }

    private void loadReviews(){
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIE_REVIEW_QUERY_EXTRA, String.valueOf(movie.getId()));
        getSupportLoaderManager().initLoader(MOVIE_REVIEW_LOADER, queryBundle, reviewLoader);
    }

    public void onClickFavourites( View v){
        if(isMovieFavourite){
            deleteMovieFormFavourites();
            mMovieFavouriteImageView.setImageResource(R.drawable.ic_star);
            isMovieFavourite = false;
        } else {
            addMovieToFavourites();
            mMovieFavouriteImageView.setImageResource(R.drawable.ic_star_filled);
            isMovieFavourite = true;
        }
    }

    public void addMovieToFavourites(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouriteMovieContract.FavouriteMovieEntry._ID, movie.getId());
        contentValues.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
        Uri uri = getContentResolver().insert(FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI, contentValues);
        if(uri != null){
            Toast.makeText(getBaseContext(), getString(R.string.movie_added_to_favourites), Toast.LENGTH_LONG).show();
        }
    }

    public void deleteMovieFormFavourites(){
        Uri movieUri = FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI.buildUpon().appendPath(String.valueOf(movie.getId())).build();
        int deleted = getContentResolver().delete(movieUri, null, null);
        if (deleted > 0) {
            Toast.makeText(getBaseContext(), getString(R.string.movie_removed_from_favourites), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onTrailerClick(MovieTrailer trailer) {
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("vnd.youtube:"
                            + trailer.getKey() ));
            startActivity(intent);
        } catch (ActivityNotFoundException e){
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=" + trailer.getKey())));
        }
    }
}
