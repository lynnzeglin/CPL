package com.lynndroid.cpl.workflow;

import android.content.Context;
import android.util.Log;

import com.lynndroid.cpl.model.Library;
import com.lynndroid.cpl.model.vieworiented.LibraryDetail;
import com.lynndroid.cpl.model.vieworiented.LibraryListItem;
import com.lynndroid.cpl.model.LookupKey;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lynnzeglin on 2/24/17.
 */

public class DataAccessor {

    private static DataAccessor sDataAccessor = null;

    // singleton getter
    public static DataAccessor getInstance() {

        if (sDataAccessor == null) {
            sDataAccessor = new DataAccessor();
        }

        return sDataAccessor;
    }

    private List<Library> mLibraryDatabase = null;


    public DataAccessor() {

    }

    /**
     * set up receivers to listen for messages from LibraryService (our api request handler)
     */

    // listener for generic message events - useful for testing
    @SuppressWarnings("unused")
    @Subscribe
    public void eventReceived(String s) {

        Log.d("CPL", "+++ library list event received in DataAccessor " + s);

    }

    // listener for list of libraries events
    @SuppressWarnings("unused")
    @Subscribe
    public void eventLibraryListReceived(ApiLibraryListEvent apiLibraryListEvent) {


        // store this latest version to database and update the freshness stamp
        storeData(apiLibraryListEvent.getList());

        // use event bus to post view oriented list to any listening views
        AndroidBus mBus = AndroidBus.getInstance();

        // activity will show this message in a toast when received (test)
        // GENERIC message event for testing
//        mBus.post("+++ view oriented list received");

        // ¯\_(ツ)_/¯
        // convert to view oriented data and post to any listeners
        mBus.post(new ViewOrientedLibraryListEvent(getLibraryItemList()));

        // unregister since done receiving events from API
        mBus.unregister(this);
    }

    /**
     * Get library list items - if data is fresh, comes from local storage
     * otherwise return what we have and make network call to refresh data
     *
     * @return whatever we have NOTE: could be empty list first time
     */
    public List<LibraryListItem> loadLibraries(Context context) {

        // figure out if our data is fresh (it will be unless first time accessing)
        // if fresh, deliver, else refresh

        // for now, just make network call (start the service that makes network call)
        // and wait for response (wait = register itself on the event bus)

        if (mLibraryDatabase == null) {
            LibraryService.startForLibraryList(context);
            AndroidBus.getInstance().register(this);
        }

        // TODO use callback to update view?
        // send view oriented version of whatever list we have - caller beware! could return empty list
        return getLibraryItemList();
    }

    /**
     * get details for a library from the database
     * @param context
     * @param lookupKey
     * @return object containing details for the library, possibly empty object if not found
     */
    public LibraryDetail loadLibraryDetail(Context context, LookupKey<String> lookupKey) {

        // TODO get library details from data storage
        LibraryDetail libraryDetail = new LibraryDetail();

        // do not search db if we have no data
        if (mLibraryDatabase == null)  {
            return libraryDetail;
        }

        // TODO if data not fresh initiate re-fetch?  but send what we have anyway for now?

        // search db for lookup key and get library details
        for (Library library : mLibraryDatabase) {
            if (library.getName().equals(lookupKey.get())) {

                libraryDetail = new LibraryDetail(
                        lookupKey,
                        library.getName(),
                        library.getPhone(),
                        library.getAddress(),
                        library.getHours(),
                        library.getLocation());
            }
        }
        return libraryDetail;
    }

    // generate view oriented models from list of models in db
    // NOTE: could return empty list
    private List<LibraryListItem> getLibraryItemList() {

        List<LibraryListItem> libraryItemList = new ArrayList<>();

        if (mLibraryDatabase == null) {
            return libraryItemList;
        }

        // fill view oriented models from database
        for (Library library : mLibraryDatabase) {
            libraryItemList.add(new LibraryListItem(
                    (new LookupKey<>(library.getName())),
                    library.getName(),
                    library.getAddress()));
        }

        return libraryItemList;
    }

    private boolean storeData(List<Library> libraries) {

        mLibraryDatabase = libraries;

        // TODO freshen the timestamp for the database
        boolean dataStored = true;

        // TODO write data to SQLite or whatever
        // TODO indicate success or failure
        return dataStored;
    }

    private boolean dataIsFresh() {
        return true;
    }

    //
    private void refreshLibraryData() {

    }

}
