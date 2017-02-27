package com.lynndroid.cpl.model.vieworiented;

import com.lynndroid.cpl.model.Location;
import com.lynndroid.cpl.model.LookupKey;

/**
 * Created by lynnzeglin on 2/24/17.
 */

public class LibraryDetail {

    private LookupKey<String> lookupKey;
    private String name;
    private String phone;
    private String address;
    private String hours;
    private Location location;

    public LibraryDetail() {

    }

    public LibraryDetail(LookupKey<String> lookupKey,
                         String name,
                         String phone,
                         String address,
                         String hours,
                         Location location) {

        this.lookupKey = lookupKey;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.hours = hours;
        this.location = location;
    }

    public LookupKey<String> getLookupKey() {
        return lookupKey;
    }

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
}
