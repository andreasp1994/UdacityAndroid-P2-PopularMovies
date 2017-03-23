package com.apogiatzis.udacitymovieproject.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andre on 17/03/2017.
 */

public class MovieReview {

    private String id;
    private String author;
    private String content;

    public MovieReview(JSONObject jsonReview) throws JSONException{
        this.id = jsonReview.getString("id");
        this.author = jsonReview.getString("author");
        this.content = jsonReview.getString("content");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
