package com.apogiatzis.udacitymovieproject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.apogiatzis.udacitymovieproject.models.Movie;
import com.apogiatzis.udacitymovieproject.persistence.FavouriteMovieContract;
import com.apogiatzis.udacitymovieproject.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class MoviesActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickListener{

    private static final String TAG = MoviesActivity.class.getSimpleName();

    private static final int FAVOURITE_MOVIE_LOADER = 19;
    private static final int MOVIE_LOADER = 20;

    private static final String MOVIE_LOADER_ARGS_CATEGORY = "MOVIE_LOADER_ARGS_CATEGORY";
    private static final String MOVIE_LOADER_ARGS_PAGE = "MOVIE_LOADER_ARGS_PAGE";

    private static final String LIFECYCLE_CALLBACK_GRID_STATE = "LIFECYCLE_CALLBACK_GRID_STATE";
    private static final String LIFECYCLE_CALLBACK_ACTIVE_CATEGORY = "LIFECYCLE_CALLBACK_ACTIVE_CATEGORY";
    private static final String LIFECYCLE_CALLBACK_ACTIVE_MOVIE_PAGE = "LIFECYCLE_CALLBACK_ACTIVE_MOVIE_PAGE";

    private RecyclerView mMoviesRecyclerView;
    private MovieAdapter mMoviesAdapter;
    private GridLayoutManager mGridLayoutManager;
    private ArrayList<Movie> mMoviesList;
    private Movie.MovieCategory activeMovieCategory;
    private int activeMoviePage;
    private Parcelable mGridState;

    private LoaderManager.LoaderCallbacks<ArrayList<Movie>> favouriteMovieLoader = new LoaderManager.LoaderCallbacks<ArrayList<Movie>>() {

        @Override
        public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle bundle) {
            return new AsyncTaskLoader<ArrayList<Movie>>(MoviesActivity.this) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    forceLoad();
                }

                @Override
                public ArrayList<Movie> loadInBackground() {
                    Uri movieUri = FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI;
                    Cursor mCursor = getContentResolver().query(movieUri, null, null, null, null);
                    ArrayList<Movie> movies = new ArrayList<>();
                    try{
                        for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
                            String movieId = mCursor.getString(mCursor.getColumnIndex(FavouriteMovieContract.FavouriteMovieEntry._ID));
                            Movie movie = NetworkUtils.fetchMovieById(movieId);
                            movies.add(movie);
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                    return movies;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
            mMoviesList.clear();
            mMoviesList.addAll(data);
            mMoviesAdapter.setMovieData(mMoviesList);
            getSupportLoaderManager().destroyLoader(FAVOURITE_MOVIE_LOADER); //This prevents onLoadFinished to be called again when the back button is pressed
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
            mMoviesAdapter.setMovieData(null);
        }
    };
    private LoaderManager.LoaderCallbacks<ArrayList<Movie>> movieLoader = new LoaderManager.LoaderCallbacks<ArrayList<Movie>>(){

        private Movie.MovieCategory mLoaderCategory;
        private ArrayList<Movie> mMovieData = null;
        int mLoaderPage;

        @Override
        public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList<Movie>>(MoviesActivity.this) {
                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if(mMovieData == null){
                        Log.i(TAG, "NULL");
                    } else {
                        Log.i(TAG, String.valueOf(mMovieData.size()));
                    }
                    mLoaderCategory = (Movie.MovieCategory) args.getSerializable(MOVIE_LOADER_ARGS_CATEGORY);
                    mLoaderPage = args.getInt(MOVIE_LOADER_ARGS_PAGE);
                    forceLoad();
                }

                @Override
                public ArrayList<Movie> loadInBackground() {
                    ArrayList<Movie> movies = null;
                    try{
                        movies = NetworkUtils.fetchMoviesByPage(mLoaderCategory, mLoaderPage);
                    } catch (IOException e){
                        e.printStackTrace();
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                    return movies;
                }

                @Override
                public void deliverResult(ArrayList<Movie> data) {
                    mMovieData = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
            if(data != null){
                mMoviesList.addAll(data);
                mMoviesAdapter.setMovieData(mMoviesList);
            } else {
                Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.movie_fetch_error_msg), Toast.LENGTH_SHORT).show();
            }
            getSupportLoaderManager().destroyLoader(MOVIE_LOADER);
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
            mMoviesAdapter.setMovieData(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        mMoviesList = new ArrayList<>();
        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.rv_movies_container);

        mGridLayoutManager = new GridLayoutManager(this, 2);
        mMoviesRecyclerView.setLayoutManager(mGridLayoutManager);
        mMoviesRecyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if((mGridLayoutManager.findLastCompletelyVisibleItemPosition() == (mMoviesList.size() -1) &&
                        (activeMovieCategory != Movie.MovieCategory.FAVOURITE) )){
                    loadMovieData(activeMovieCategory, activeMoviePage+1);
                }
            }
        });
        mMoviesRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MovieAdapter(this);
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);

        if(NetworkUtils.isOnline(this)){
            loadMovieData(Movie.MovieCategory.TOP_RATED, 1);
        } else {
            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.movie_fetch_connection_error_msg),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(activeMovieCategory == Movie.MovieCategory.FAVOURITE){
            getSupportLoaderManager().initLoader(FAVOURITE_MOVIE_LOADER, null, favouriteMovieLoader);
        } else {
            Bundle args = new Bundle();
            args.putSerializable(MOVIE_LOADER_ARGS_CATEGORY, activeMovieCategory);
            args.putInt(MOVIE_LOADER_ARGS_PAGE, activeMoviePage);
            getSupportLoaderManager().initLoader(MOVIE_LOADER, args, movieLoader );
        }
        mGridLayoutManager.onRestoreInstanceState(mGridState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mGridState = mGridLayoutManager.onSaveInstanceState();
        outState.putParcelable(LIFECYCLE_CALLBACK_GRID_STATE, mGridState);
        outState.putSerializable(LIFECYCLE_CALLBACK_ACTIVE_CATEGORY, activeMovieCategory);
        outState.putInt(LIFECYCLE_CALLBACK_ACTIVE_MOVIE_PAGE, activeMoviePage);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null){
            mGridState = savedInstanceState.getParcelable(LIFECYCLE_CALLBACK_GRID_STATE);
            activeMovieCategory = (Movie.MovieCategory) savedInstanceState.getSerializable(LIFECYCLE_CALLBACK_ACTIVE_CATEGORY);
            activeMoviePage = savedInstanceState.getInt(LIFECYCLE_CALLBACK_ACTIVE_MOVIE_PAGE);
        }
    }

    public void loadMovieData(Movie.MovieCategory category, int page){
        activeMovieCategory = category;
        activeMoviePage = page;
        Bundle args = new Bundle();
        args.putSerializable(MOVIE_LOADER_ARGS_CATEGORY, activeMovieCategory);
        args.putInt(MOVIE_LOADER_ARGS_PAGE, activeMoviePage);
        getSupportLoaderManager().initLoader(MOVIE_LOADER, args, movieLoader);
    }

    public void loadFavouriteMovies(){
        activeMovieCategory = Movie.MovieCategory.FAVOURITE;
        getSupportLoaderManager().initLoader(FAVOURITE_MOVIE_LOADER, null, favouriteMovieLoader);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        mMoviesList.clear();
        if (id == R.id.movies_menu_popular) {
            mMoviesAdapter.setMovieData(null);
            loadMovieData(Movie.MovieCategory.POPULAR, 1);
            return true;
        } else if(id == R.id.movies_menu_top_rated) {
            mMoviesAdapter.setMovieData(null);
            loadMovieData(Movie.MovieCategory.TOP_RATED, 1);
            return true;
        } else if(id == R.id.movies_menu_favourite) {
            mMoviesAdapter.setMovieData(null);
            loadFavouriteMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(MoviesActivity.this, MovieDetailsActivity.class);
        intent.putExtra(Movie.INTENT_EXTRA_MOVIE_OBJECT, movie);
        startActivity(intent);
    }

}
