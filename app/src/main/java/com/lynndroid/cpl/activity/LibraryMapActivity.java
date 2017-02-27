package com.lynndroid.cpl.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lynndroid.cpl.R;

import static com.lynndroid.cpl.R.id.map;

/**
 * Activity to show details about library.  Shows pin on map, address, hours of operation
 * and allows user to call the library.  Does some basic error checking for intent extras and
 * sets defaults if not found.
 *
 * NOTE:  phone number is not checked for valid format
 * TODO if passing more data than this, probably want to make the Library object Parcelable
 */
public class LibraryMapActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String EXTRA_LIB_NAME = "library name";
    private static final String EXTRA_LIB_PHONE = "library phone number";
    private static final String EXTRA_LIB_HOURS = "library hours of operation";
    private static final String EXTRA_LIB_MAP_LAT = "library map latitude";
    private static final String EXTRA_LIB_MAP_LON = "library map longitude";

    // default values
    private static final String NO_PHONE = "No phone";
    private static final String HOURS_UNKNOWN = "Call for hours";

    /**
     * convenience method for starting this activity
     * @param caller activity that is starting this activity
     * @param name name of library
     * @param phone library phone number
     * @param hours library hours of operation
     * @param lat library latitude
     * @param lon library longitude
     */
    public static void start(Activity caller, String name, String phone, String hours, Double lat, Double lon) {

        Intent intent = new Intent(caller, LibraryMapActivity.class);
        intent.putExtra(EXTRA_LIB_NAME, name);
        intent.putExtra(EXTRA_LIB_PHONE, phone);
        intent.putExtra(EXTRA_LIB_HOURS, hours);
        intent.putExtra(EXTRA_LIB_MAP_LAT, lat);
        intent.putExtra(EXTRA_LIB_MAP_LON, lon);
        caller.startActivity(intent);
    }

    private String mLibName;
    private String mLibPhone;
    private Double mLat;
    private Double mLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Double LAT_DEFAULT = 0.0000;
        Double LON_DEFAULT = 0.0000;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // get extras
        mLat = getIntent().getDoubleExtra(EXTRA_LIB_MAP_LAT, LAT_DEFAULT);
        mLon = getIntent().getDoubleExtra(EXTRA_LIB_MAP_LON, LON_DEFAULT);

        // marker will not show name if it's null so make sure it has something
        if (getIntent().hasExtra(EXTRA_LIB_NAME)) {
            mLibName = getString(R.string.title_activity_maps,
                    getIntent().getStringExtra(EXTRA_LIB_NAME));
        }
        else {
            mLibName = "Library";
        }

        // put library name in action bar
        setTitle(mLibName);

        // tapping on info window will dial phone number
        if (getIntent().hasExtra(EXTRA_LIB_PHONE)) {
            mLibPhone = getIntent().getStringExtra(EXTRA_LIB_PHONE);
            // TODO validate correct 10 digit number format
        }
        else {
            mLibName = NO_PHONE;
        }

        // get hours of operation
        String libHours;
        if (getIntent().hasExtra(EXTRA_LIB_HOURS)) {
            libHours = getIntent().getStringExtra(EXTRA_LIB_HOURS);
        }
        else {
            libHours = HOURS_UNKNOWN;
        }

        // show library hours of operation
        TextView txtLibHours = (TextView) findViewById(R.id.txt_hours);

        // make hours a little more readable - it's not perfect because the source data isn't perfect
        libHours = libHours.replace("; ", "\n");
        libHours = libHours.replace(";", "\n");
        txtLibHours.setText(getString(R.string.hours_of_operation, libHours));

        // request map and be notified when it is ready to be used - see onMapReady()
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);

        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // get library lat/lon and show marker there
        // NOTE:  lat/lon probably comes from Mapquest so marker will likely
        // not coincide exactly with Google Map data for library's lat/lon
        LatLng libraryLoc = new LatLng(mLat, mLon);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(libraryLoc)
                .title(mLibName)
                .snippet(getString(R.string.tap_to_call)));

        // don't wait for tap on marker, just show info window right away
        marker.showInfoWindow();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(libraryLoc)                         // centered on lat/lon provided
                .zoom(googleMap.getMaxZoomLevel() * .65f)   // set zoom to % of max
                .tilt(30)                                   // 30 degrees
                .build();

        // here we go!
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // tap on info window to call the library (if device can call)
        // Note: phone number has not been validated for correct format
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (!mLibPhone.equals(NO_PHONE)) {
                    Intent callIntent =
                            new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:" + mLibPhone));
                    if (callIntent.resolveActivity(
                            getApplicationContext().getPackageManager()) != null) {
                        LibraryMapActivity.this.startActivity(callIntent);
                    }
                    else {
                        Toast.makeText(LibraryMapActivity.this, "Device cannot make calls",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(LibraryMapActivity.this, "phone number invalid",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * hide/show info window when MARKER is clicked
     * NOTE: this doesn't work consistently - seems affected by zoom level or ??
     * @param marker map pin
     * @return true to indicate the click was handled
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        }
        else {
            marker.showInfoWindow();
        }
        return true;
    }
}