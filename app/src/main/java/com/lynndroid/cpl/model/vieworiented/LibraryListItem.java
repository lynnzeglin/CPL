package com.lynndroid.cpl.model.vieworiented;

import com.lynndroid.cpl.model.LookupKey;

/**
 * Created by lynnzeglin on 2/24/17.
 */

public class LibraryListItem {

    private LookupKey<String> lookupKey;
    private String name;
    private String address;

    public LibraryListItem() {
    }

    public LibraryListItem(LookupKey<String> lookupKey, String name, String address) {
        this.lookupKey = lookupKey;
        this.name = name;
        this.address = address;
    }

    public LookupKey<String> getLookupKey() {
        return lookupKey;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
