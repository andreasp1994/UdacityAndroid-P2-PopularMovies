package com.apogiatzis.udacitymovieproject.persistence;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by andre on 15/03/2017.
 */

public class FavouriteMovieContract {

    public static final String AUTHORITY = "com.apogiatzis.udacitymovieproject";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "favouriteMovies";

    private FavouriteMovieContract(){};

    public static final class FavouriteMovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "favouriteMovies";
        public static final String COLUMN_MOVIE_TITLE = "movieTitle";
    }
}
