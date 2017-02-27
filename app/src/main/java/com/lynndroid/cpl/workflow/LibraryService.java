package com.lynndroid.cpl.workflow;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lynndroid.cpl.model.Library;
import com.lynndroid.cpl.model.LookupKey;
import com.squareup.otto.Bus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static com.lynndroid.cpl.workflow.LibraryClient.BASE_URL;

/**
 * Basically this is the API request handler - currently known as 'the workflow'
 */

public class LibraryService extends IntentService {

    // name for this service
    public static final String SERVICE_LIBRARY_WORKFLOW = "library workflow service";

    // intents and extras handled by this service

    public static final String REQUEST_TYPE_LIBRARIES_LIST = "request list of libraries";
    public static final String REQUEST_TYPE_LIBRARY_DETAILS = "request details for one library";

    public static final String EXTRA_REQUEST_TYPE = "type of request";
    public static final String EXTRA_LIBRARY_LOOKUPKEY = "lookup key for library";

    // otto events delivered
    private static final String EVENT_LIBRARY_LIST_RESPONSE = "list of all libraries";

    // TODO api response statuses
    private static final String STATUS_RESPONSE_SUCCESS = "library api responded";
    private static final String STATUS_RESPONSE_FAIL = "library api response failure";
    private static final String STATUS_RESPONSE_TIMEOUT = "library api response timeout";

    // fields
//    private List<Library> mLibraryList = null;
    private Bus mBus;

    public LibraryService() {
        super(SERVICE_LIBRARY_WORKFLOW);
        mBus = AndroidBus.getInstance();
    }

    // use this to start service to fetch list of libraries
    public static void startForLibraryList(Context context) {
        Intent intent = new Intent(context, LibraryService.class);
        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_TYPE_LIBRARIES_LIST);
        context.startService(intent);

        Log.d("CPL", "+++ Library Workflow Service started to fetch library list");
    }

    // use this to start service to fetch details for one library
    public static void startForLibraryDetails(Context context, LookupKey<Integer> lookupKey) {
        Intent intent = new Intent(context, LibraryService.class);

        intent.putExtra(EXTRA_REQUEST_TYPE, REQUEST_TYPE_LIBRARY_DETAILS);
        intent.putExtra(EXTRA_LIBRARY_LOOKUPKEY, lookupKey.get());
        context.startService(intent);

        Log.d("CPL", "+++ Library Workflow Service started to fetch one library's details");
    }

    // TODO create [multiple?) @Producer to provide last known results (of list? library details?) immediately upon registering
    // get current value from SQLite if exists else no current value?

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent.hasExtra(EXTRA_REQUEST_TYPE)) {
            String reqType = intent.getStringExtra(EXTRA_REQUEST_TYPE);

            // fetch all libraries
            if (reqType.equals(REQUEST_TYPE_LIBRARIES_LIST)) {
                Log.d("CPL", "+++ fetching library list");
                fetchLibraryList();
                return;
            }

            // fetch one library
            if (reqType.equals(REQUEST_TYPE_LIBRARY_DETAILS)) {
                if (intent.hasExtra(EXTRA_LIBRARY_LOOKUPKEY)) {
                    String lookupKey = intent.getStringExtra(EXTRA_LIBRARY_LOOKUPKEY);
                    Log.d("CPL", String.format("+++ fetch library with lookup key %s", lookupKey));
                    // TODO
                    // fetchLibraryDetail(lookupKey);
                }
                else {
                    Log.d("CPL", "+++ received intent with missing extras");
                }
                return;
            }
        }

        // unexpected intent received
        Log.d("CPL", "+++ received unexpected intent");
    }

    /**
     * make api call to get the latest list of libraries
     */
    private void fetchLibraryList() {

        // get list of libraries
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LibraryClient libraryClient = retrofit.create(LibraryClient.class);
        Call<List<Library>> librariesCall = libraryClient.getLibraries();

        // enqueue makes an async call, but onResponse and onFailure conveniently execute on the UI thread
        librariesCall.enqueue(new Callback<List<Library>>() {
            @Override
            public void onResponse(Call<List<Library>> call, Response<List<Library>> response) {
                // check for successful response and successful decoding of response.body()
                if (response.isSuccessful() && response.body() != null) {

                    deliverResults(response.body());
                }
                else {
                    // TODO handle this better
                    Timber.d("API responded but with error - list of libraries unavailable");
                }
            }

            @Override
            public void onFailure(Call<List<Library>> call, Throwable t) {
                // TODO handle this better
                Timber.d("API call failed - cannot fetch list of libraries");
            }
        });
    }

    // deliver results back to subsriber(s) i.e. DataAccessor
    private void deliverResults(List<Library> libraryList) {

        //
//        mBus.post("+++ testing delivery of message from Service to DataAccessor");

        mBus.post(new ApiLibraryListEvent(libraryList));
//        Timber.d("+++ results delivered");
    }


    // TODO move:  Service doesn't store, DataAccessor does
//    /**
//     * store results to device for offline functionality and deliver the
//     * results to any subscribers
//     */
//    private void storeAndDeliverResults(List<Library> listToStore) {
//
//        if (listToStore == null) {
//            // hmmm, didn't get a list, deliver a message
//            mBus.post("+++ no results to deliver (yet)");
//            return;
//        }
//
//        // TODO write results to SQLite db
//
//        // TODO worry if write to device db fails
//        mLibraryList = listToStore;
//
//        // deliver results to any subscribers
//        mBus.post(new ApiLibraryListEvent(mLibraryList));
//        Timber.d("+++ results delivered");
//    }
}
