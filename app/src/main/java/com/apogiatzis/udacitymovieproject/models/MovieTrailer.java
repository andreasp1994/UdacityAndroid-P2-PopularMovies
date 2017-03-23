package com.apogiatzis.udacitymovieproject.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by andre on 15/03/2017.
 */

public class MovieTrailer {

    private String id;
    private String key;
    private String site;
    private String name;

    public MovieTrailer(JSONObject jsonTrailer) throws JSONException{
        this.id = jsonTrailer.getString("id");
        this.key = jsonTrailer.getString("key");
        this.name = jsonTrailer.getString("name");
        this.site = jsonTrailer.getString("site");
    }

    public MovieTrailer(String id, String key, String site, String name){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
