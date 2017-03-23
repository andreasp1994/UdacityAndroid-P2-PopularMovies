package com.apogiatzis.udacitymovieproject.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by andre on 16/01/2017.
 */

public class Movie implements Parcelable {

    public static String MOVIE_POSTER_DEFAULT_SIZE = "w185";

    public static String INTENT_EXTRA_MOVIE_OBJECT = "INTENT_EXTRA_MOVIE_OBJECT";

    public enum MovieCategory {
        POPULAR, TOP_RATED, FAVOURITE
    }

    private String posterPath;
    private boolean adult;
    private String overview;
    private String releaseDate;
    private ArrayList<Integer> genresList;
    private int id;
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private String backdropPath;
    private double popularity;
    private boolean video;
    private int voteCount;
    private double voteAverage;

    public Movie(JSONObject movieObject) throws JSONException{
        this.posterPath = movieObject.getString("poster_path");
        this.adult = movieObject.getBoolean("adult");
        this.overview = movieObject.getString("overview");
        this.releaseDate = movieObject.getString("release_date");
        JSONArray genres = movieObject.getJSONArray("genre_ids");
        genresList = new ArrayList<>();
        for(int i =0;i < genres.length(); i++){
            genresList.add(genres.getInt(i));
        }
        this.id = movieObject.getInt("id");
        this.originalTitle = movieObject.getString("original_title");
        this.originalLanguage = movieObject.getString("original_language");
        this.title = movieObject.getString("title");
        this.backdropPath = movieObject.getString("backdrop_path");
        this.popularity = movieObject.getDouble("popularity");
        this.voteCount = movieObject.getInt("vote_count");
        this.video = movieObject.getBoolean("video");
        this.voteAverage = movieObject.getDouble("vote_average");
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public ArrayList<Integer> getGenresList() {
        return genresList;
    }

    public void setGenresList(ArrayList<Integer> genresList) {
        this.genresList = genresList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    /*
    Parcelable implementation
     */
    protected Movie(Parcel in) {
        posterPath = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
        releaseDate = in.readString();
        id = in.readInt();
        originalTitle = in.readString();
        originalLanguage = in.readString();
        title = in.readString();
        backdropPath = in.readString();
        popularity = in.readDouble();
        video = in.readByte() != 0;
        voteCount = in.readInt();
        voteAverage = in.readDouble();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(posterPath);
        parcel.writeByte((byte) (adult ? 1 : 0));
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeInt(id);
        parcel.writeString(originalTitle);
        parcel.writeString(originalLanguage);
        parcel.writeString(title);
        parcel.writeString(backdropPath);
        parcel.writeDouble(popularity);
        parcel.writeByte((byte) (video ? 1 : 0));
        parcel.writeInt(voteCount);
        parcel.writeDouble(voteAverage);
    }
}
