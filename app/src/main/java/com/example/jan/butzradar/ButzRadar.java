package com.example.jan.butzradar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class ButzRadar extends AppCompatActivity {

    private final static String CLASS_ID = "BUTZRADAR";

    private MapViewer mapViewer;
    private LocationPollingScheduler locationPollingScheduler;
    private PositioningScheduler positioningScheduler;
    private RadarSharedPreferences radarSharedPreferences;
    private boolean positioningIsActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_main);

        createMapFragment();

        initFABListener();

        new ServerLocationUpdateReceiver(this);

        radarSharedPreferences = new RadarSharedPreferences(this);

        positioningIsActive = radarSharedPreferences.getPositioningSetting();

        if (positioningIsActive)
            startPositioning();
    }

    @Override
    protected void onResume() {
        super.onResume();

        manualLocationPoll();

        startLocationPolling();
        Log.i(CLASS_ID, "Location polling started.");
    }

    private void manualLocationPoll() {
        Intent startLocationPoll = new Intent(this, LocationPoller.class);
        this.startService(startLocationPoll);
    }

    private void createMapFragment() {

        mapViewer = new MapViewer(this);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem positioningItem = menu.findItem(R.id.location_toggle);
        int icon;

        if (positioningIsActive)
            icon = R.drawable.ic_location_on_white_24dp;
        else
            icon = R.drawable.ic_location_off_white_24dp;

        positioningItem.setIcon(icon);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.butz_radar_menu, menu);

        int positioningIcon;

        if (positioningIsActive()) {
            positioningIcon = R.drawable.ic_gps_fixed_white_24dp;
        } else {
            positioningIcon = R.drawable.ic_gps_off_white_24dp;
        }

        menu.findItem(R.id.location_toggle).setIcon(positioningIcon);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.focus_marker:
                mapViewer.focusCameraOnMarker();
                return true;
            case R.id.location_toggle:
                togglePositioning();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean positioningIsActive() {
        SharedPreferences settings = getPreferences(0);
        return settings.getBoolean("positioningActive", true);
    }

    public void startLocationPolling() {
        locationPollingScheduler = new LocationPollingScheduler(this);
        locationPollingScheduler.startPollingAlarm();
    }

    private void initFABListener() {

        FloatingActionButton myFab = (FloatingActionButton) this.findViewById(R.id.bounds_fab);
        myFab.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mapViewer.changeCameraToBounds();
            }

        });

    }

    private void togglePositioning() {
        if (positioningIsActive)
            stopPositioning();
        else
            startPositioning();

        positioningIsActive = !positioningIsActive;
        supportInvalidateOptionsMenu();
    }

    private void startPositioning() {

        positioningScheduler = new PositioningScheduler(this);
        positioningScheduler.startPositioningAlarm();

        String snackBarText = "Standortbestimmung gestartet";
        Snackbar.make(findViewById(R.id.container), snackBarText, Snackbar.LENGTH_LONG)
                .show();
    }

    public void initiateMarkerReplace(LocationEntry[] locationEntries) {
        mapViewer.replaceLocationMarkers(locationEntries);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i("DESTROY", "On destroy called");
    }

    @Override
    protected void onStop() {
        super.onStop();

        locationPollingScheduler.cancelLocationPolling();
        Log.i(CLASS_ID, "Location polling stopped.");

        radarSharedPreferences.setPositioningSetting(positioningIsActive);
    }

    private void stopPositioning() {

        positioningScheduler.cancelPositioning();
        Log.i(CLASS_ID, "Positioning stopped.");

        String snackBarText = "Standortbestimmung gestoppt";
        Snackbar.make(findViewById(R.id.container), snackBarText, Snackbar.LENGTH_LONG)
                .show();
    }
}
