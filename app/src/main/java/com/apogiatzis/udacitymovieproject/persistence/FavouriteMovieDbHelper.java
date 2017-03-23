package com.apogiatzis.udacitymovieproject.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by andre on 15/03/2017.
 */

public class FavouriteMovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 2;

    public FavouriteMovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME + " (" +
                FavouriteMovieContract.FavouriteMovieEntry._ID  + " TEXT PRIMARY KEY, " +
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL " +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
