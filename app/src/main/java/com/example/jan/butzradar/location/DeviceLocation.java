package com.example.jan.butzradar.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.example.jan.butzradar.preferences.RadarSharedPreferences;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class DeviceLocation extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static String CLASS_ID = "DEVICE_LOCATION";
    private final static float GPS_ACCURACY_THRESHOLD = 100.0f;
    private final static int MAX_LOCATION_UPDATES = 3;
    private final static long LOCATION_TIMEOUT = 10000;
    private final static long LOCATION_TIMEOUT_TICKS = 1000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean currentlyPositioning = false;
    private int numberOfLocationUpdates;
    private Location mostAccuratePosition;
    private CountDownTimer countDownTimer;

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
        countDownTimer = new CountDownTimer(LOCATION_TIMEOUT, LOCATION_TIMEOUT_TICKS) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i(CLASS_ID, "Seconds remaining for positioning: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                Log.i(CLASS_ID, "Positioning timeout.");
                finishPositioning();
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(CLASS_ID, "Location changed. " + location.toString());
        numberOfLocationUpdates++;

        if (mostAccuratePosition == null || mostAccuratePosition.getAccuracy() <= location.getAccuracy()) {
            mostAccuratePosition = location;
        }

        if (mostAccuratePosition.getAccuracy() < GPS_ACCURACY_THRESHOLD || numberOfLocationUpdates >= MAX_LOCATION_UPDATES) {
            countDownTimer.cancel();
            finishPositioning();
        }
    }

    private void finishPositioning() {
        if (mostAccuratePosition != null) {
            RadarSharedPreferences radarSharedPreferences = new RadarSharedPreferences(this);
            radarSharedPreferences.setOwnLocation(new LatLng(mostAccuratePosition.getLatitude(), mostAccuratePosition.getLongitude()));
            startUploading(mostAccuratePosition);
        }
        stopLocationUpdates();
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
