package com.skyline.kattaadminapp;

import com.google.gson.JsonObject;

import org.json.JSONException;

/**
 * Created by ameyaapte1 on 18/5/16.
 */
public interface AsyncTaskComplete {
    public void handleResult(JsonObject input, JsonObject result, String action) throws JSONException;
}
