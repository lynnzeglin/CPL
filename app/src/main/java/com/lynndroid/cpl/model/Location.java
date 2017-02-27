package com.lynndroid.cpl.model;

import com.google.gson.annotations.SerializedName;

/**
 * Location stores a latitude/longitude pair
 */

public class Location {
    private @SerializedName("latitude") Double lat;
    private @SerializedName("longitude") Double lon;

    public Location() {
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }



}
