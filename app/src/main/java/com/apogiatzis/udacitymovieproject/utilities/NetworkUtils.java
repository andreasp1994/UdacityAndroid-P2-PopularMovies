package com.apogiatzis.udacitymovieproject.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.apogiatzis.udacitymovieproject.BuildConfig;
import com.apogiatzis.udacitymovieproject.models.Movie;
import com.apogiatzis.udacitymovieproject.models.MovieReview;
import com.apogiatzis.udacitymovieproject.models.MovieTrailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by andre on 16/01/2017.
 */

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIES_BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String MOVIES_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    private static String API_KEY =  BuildConfig.API_KEY;

    public static ArrayList<MovieTrailer> fetchMovieTrailers(String movieId) throws IOException, JSONException {
        URL url = buildMovieTrailerUrl(movieId);
        String jsonMovieTrailerData = getResponseFromHttpUrl(url);

        JSONObject trailerData = new JSONObject(jsonMovieTrailerData);
        JSONArray trailers = trailerData.getJSONArray("results");
        ArrayList<MovieTrailer> movieTrailerList = new ArrayList<MovieTrailer>();
        for(int i = 0; i < trailers.length();i++){
            MovieTrailer movieTrailer = new MovieTrailer(trailers.getJSONObject(i));
            // Get only trailers from Youtube
            if(movieTrailer.getSite().equals("YouTube")){
                movieTrailerList.add(movieTrailer);
            }
        }

        return movieTrailerList;
    }

    public static ArrayList<Movie> fetchMoviesByPage(Movie.MovieCategory category, int page) throws IOException, JSONException{
        URL url = buildMoviesUrl(category, page);
        String jsonMovieData = getResponseFromHttpUrl(url);

        //Convert json response to java objects
        JSONObject movieData = new JSONObject(jsonMovieData);
        JSONArray movies = movieData.getJSONArray("results");
        ArrayList<Movie> movieList = new ArrayList<Movie>();
        for(int i = 0 ; i < movies.length();i++){
            Movie movie = new Movie(movies.getJSONObject(i));
            movieList.add(movie);
        }

        return movieList;
    }

    public static Movie fetchMovieById(String movieId) throws IOException, JSONException{
        URL url = buildMovieUrl(movieId);
        String jsonMovieData = getResponseFromHttpUrl(url);

        //Fix API inconsistencies here so I can use JSON object in Movie constructor
        JSONObject movieData = new JSONObject(jsonMovieData);
        movieData.put("id", movieId);
        JSONArray genres = movieData.getJSONArray("genres");
        JSONArray genres_ids = new JSONArray();
        for(int i = 0;i < genres.length();i++){
            int genre_id = genres.getJSONObject(i).getInt("id");
            genres_ids.put(genre_id);
        }
        movieData.put("genre_ids", genres_ids);
        return new Movie(movieData);
    }

    public static ArrayList<MovieReview> fetchMovieReviews(String movieId) throws  IOException,JSONException {
        URL url = buildMovieReviewUrl(movieId);
        String jsonMovieReviewData = getResponseFromHttpUrl(url);
        Log.i("DEBUG", jsonMovieReviewData);
        JSONObject reviewData = new JSONObject(jsonMovieReviewData);
        JSONArray reviews = reviewData.getJSONArray("results");
        ArrayList<MovieReview> movieReviewList = new ArrayList<>();
        for(int i = 0; i < reviews.length();i++){
            MovieReview movieReview = new MovieReview(reviews.getJSONObject(i));
            movieReviewList.add(movieReview);
        }

        return movieReviewList;
    }

    private static URL buildMovieReviewUrl(String movieId){
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath("reviews")
                .appendQueryParameter("api_key", API_KEY )
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    private static URL buildMovieTrailerUrl(String movieId){
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendPath(movieId)
                        .appendPath("videos")
                        .appendQueryParameter("api_key", API_KEY )
                        .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    private static URL buildMovieUrl(String movieId){
        Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter("api_key", API_KEY )
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    private static URL buildMoviesUrl(Movie.MovieCategory category, int page) {
        Uri builtUri = Uri.parse(MOVIES_BASE_URL);

        switch (category){
            case TOP_RATED:
                builtUri = builtUri.buildUpon()
                        .appendEncodedPath("top_rated")
                        .build();
                break;
            case POPULAR:
                builtUri = builtUri.buildUpon()
                        .appendEncodedPath("popular")
                        .build();
                break;
        }

        builtUri = builtUri.buildUpon().appendQueryParameter("api_key", API_KEY )
                                    .appendQueryParameter("page", String.valueOf(page))
                                    .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static URL buildMoviePosterUrl(String posterPath, String size){
        Uri builtUri = Uri.parse(MOVIES_POSTER_BASE_URL).buildUpon()
                        .appendEncodedPath(size)
                        .appendPath(posterPath.substring(1))
                        .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static URL buildMovieTrailerThumbUrl(String trailerKey){
        String YOUTUBE_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi";
        Uri builtUri = Uri.parse(YOUTUBE_THUMBNAIL_BASE_URL).buildUpon()
                .appendPath(trailerKey)
                .appendEncodedPath("1.jpg")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static boolean isOnline(Context c) {
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}
