package com.lynndroid.cpl.model;

import com.google.gson.annotations.SerializedName;


/**
 * This is the model that comes directly from the API
 * stores some basic info about a Chicago library
 * address is street only and does not include city or state
 */

public class Library {

    // lookup key is name for our purposes
    private @SerializedName("name_") String name;
    private String phone;
    private String address;
    private @SerializedName("hours_of_operation") String hours;
    private @SerializedName("location") Location location;

    public Library() {
    }

    public Library(
            String name,
            String phone,
            String address,
            String hours,
            Location location) {

        this.name = name;
        this.phone = phone;
        this.address = address;
        this.hours = hours;
        this.location = location;
    }

    //region getters

    public String getName() {
            return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getHours() {
        return hours;
    }

    public Location getLocation() {
        return location;
    }

    //endregion getters
}
