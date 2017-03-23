package com.apogiatzis.udacitymovieproject.persistence;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by andre on 15/03/2017.
 */

public class FavouriteMovieContentProvider extends ContentProvider {

    public static final int FAVOURITE_MOVIES = 100;
    public static final int FAVOURITE_MOVIES_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavouriteMovieContract.AUTHORITY, FavouriteMovieContract.PATH_MOVIES, FAVOURITE_MOVIES);
        uriMatcher.addURI(FavouriteMovieContract.AUTHORITY, FavouriteMovieContract.PATH_MOVIES + "/#", FAVOURITE_MOVIES_WITH_ID);
        return uriMatcher;
    }

    private FavouriteMovieDbHelper mFavouriteMovieDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavouriteMovieDbHelper = new FavouriteMovieDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db =   mFavouriteMovieDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor returnCursor;

        switch (match){
            case FAVOURITE_MOVIES:
                returnCursor = db.query(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case FAVOURITE_MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id=?";
                String[] mSelectionArgs =  new String[]{id};

                returnCursor = db.query(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri.toString());
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db =   mFavouriteMovieDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match){
            case FAVOURITE_MOVIES:
                long id = db.insert(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME, null, contentValues);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri.toString());
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        final SQLiteDatabase db = mFavouriteMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int moviesDeleted;
        switch (match) {
            case FAVOURITE_MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);
                moviesDeleted = db.delete(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME,
                        "_id=?",
                        new String[]{id});
                if(moviesDeleted == 0 ){
                    throw new android.database.SQLException("Failed to delete favourite movie!");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:+" + uri.toString());
        }

        if (moviesDeleted != 0) {
            // A task was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return moviesDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
