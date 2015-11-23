package com.example.jan.butzradar;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class DeviceLocation extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static String CLASS_ID = "DEVICE_LOCATION";
    private final static float GPS_ACCURACY_THRESHOLD = 100.0f;
    private final static int MAX_LOCATION_UPDATES = 5;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Intent intent;
    private boolean currentlyPositioning = false;
    private int numberOfLocationUpdates = 0;

    public DeviceLocation() {
        super("DeviceLocation");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(CLASS_ID, "DeviceLocation service started.");
        this.intent = intent;

        buildGoogleApiClient();
        createLocationRequest();

        if (!currentlyPositioning) {
            currentlyPositioning = true;
            startPositioning();
        }
    }

    private void startPositioning() {

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            buildGoogleApiClient();

            if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }

        } else {
            Log.e(CLASS_ID, "Unable to connect to Google Play Services.");
        }

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(CLASS_ID, "Connected to Google API.");

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(CLASS_ID, "Location changed. " + location.toString());
        numberOfLocationUpdates++;

        if (location.getAccuracy() < GPS_ACCURACY_THRESHOLD || numberOfLocationUpdates >= MAX_LOCATION_UPDATES) {
            startUploading(location);
            stopLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    private void startUploading(Location location) {
        Intent locationUpdateIntent = new Intent(this, LocationUploader.class);
        locationUpdateIntent.putExtra("location", location);
        startService(locationUpdateIntent);
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopSelf();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        stopLocationUpdates();
    }

}
