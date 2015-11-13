package com.example.jan.butzradar;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Jan on 08.11.2015.
 */
public class DeviceLocation extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static String CLASS_ID = "DEVICE_LOCATION";
    private final static float GPS_ACCURACY_THRESHOLD = 500.0f;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(CLASS_ID, "DeviceLocation service started.");

        buildGoogleApiClient();
        createLocationRequest();

        if (mGoogleApiClient.isConnected() == false)
            mGoogleApiClient.connect();

        PositioningAlarmReceiver.completeWakefulIntent(intent);

        return START_NOT_STICKY;
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
        Log.i(CLASS_ID, "Location changed. Accuracy: " + location.getAccuracy());
        if (location.getAccuracy() < GPS_ACCURACY_THRESHOLD) {
            startUploading(location);
            stopLocationUpdates();
        }
    }

    private void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        stopSelf();
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
    }

}
