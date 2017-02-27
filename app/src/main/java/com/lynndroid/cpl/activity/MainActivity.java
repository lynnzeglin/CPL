package com.lynndroid.cpl.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lynndroid.cpl.R;
import com.lynndroid.cpl.adapter.LibraryAdapter;
import com.lynndroid.cpl.model.vieworiented.LibraryDetail;
import com.lynndroid.cpl.model.vieworiented.LibraryListItem;
import com.lynndroid.cpl.workflow.ViewOrientedLibraryListEvent;
import com.lynndroid.cpl.workflow.AndroidBus;
import com.lynndroid.cpl.workflow.Workflow;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Launcher activity - fetches and displays list of public libraries in Chicago
 * minimal error checking is done and there is no load progress view
 */
public class MainActivity extends AppCompatActivity implements LibraryAdapter.ItemClickListener {

    private RecyclerView mRecyclerView;
    private LibraryAdapter mAdapter;
    private Workflow mWorkflow;
    private List<LibraryListItem> mLibraryItemList = null;

    @Override
    protected void onStart() {
        super.onStart();



    }

    @Override
    protected void onResume() {
        super.onResume();

        // register to receive events from the LibraryService
        AndroidBus.getInstance().register(this);
    }

    /**
     * set up receivers to listen for messages from LibraryService (our api request handler)
     */

    // listener for generic message events
    @SuppressWarnings("unused")
    @Subscribe
    public void eventReceived(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    // listener for list of libraries events
    @SuppressWarnings("unused")
    @Subscribe
    public void eventReceived(ViewOrientedLibraryListEvent libraryListEvent) {
        mLibraryItemList = libraryListEvent.getList();

        updateLibraryListUI();

        // TODO remove debug msgs
        Log.d("CPL", "+++ library list (view oriented) event received in MainActivity");
//        Toast.makeText(this, "library list received in MainActivity", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up adapter
        mRecyclerView = (RecyclerView) findViewById(R.id.library_recycler_view);

        // allows RecyclerView to avoid invalidating the whole layout when its adapter contents change
        mRecyclerView.setHasFixedSize(true);

        // layout manager must be provided for RecyclerView to function
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // use empty adapter until we have our data
        mAdapter = new LibraryAdapter();
        mRecyclerView.setAdapter(mAdapter);

        // TODO don't reinstantiate if killed
        mWorkflow = new Workflow(this);

        /**
         // startForLibraryList service which will fetch the libraries - UI is updated after results delivered
         LibraryService.startForLibraryList(this);
         *
         */
        mLibraryItemList = mWorkflow.loadLibraryList();

        // TODO: detect on UP or BACK button pressed and cancel API call with librariesCall.cancel();
    }


    @Override
    protected void onPause() {
        super.onPause();
        AndroidBus.getInstance().unregister(this);
    }

    /**
     * The onItemClick implementation of the LibraryAdapter item click
     */
    @Override
    public void onItemClick(View view, int position) {
        final LibraryDetail libraryDetail =
                mWorkflow.loadLibraryDetail(mLibraryItemList.get(position).getLookupKey());

        if (libraryDetail == null) {
            return;
        }

        Toast.makeText(MainActivity.this, getString(R.string.fun_time), Toast.LENGTH_SHORT).show();
        LibraryMapActivity.start(
                MainActivity.this,
                libraryDetail.getName(),
                libraryDetail.getPhone(),
                libraryDetail.getHours(),
                libraryDetail.getLocation().getLat(),
                libraryDetail.getLocation().getLon());
    }

    /**
     * Update UI and wire up recycler view to adapter
     */
    private void updateLibraryListUI() {

        // show total number of libraries in list header
        TextView listHeader = (TextView) findViewById(R.id.text_list_header);
        listHeader.setText(getString(R.string.library_list_header, mLibraryItemList.size()));

        // set data for adapter
        mAdapter.setData(mLibraryItemList);
        mAdapter.setItemClickListener(MainActivity.this);
    }
}