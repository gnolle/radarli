package com.example.jan.butzradar;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class DeviceLocation extends Service implements ConnectionCallbacks, OnConnectionFailedListener {

    private final static String CLASS_ID = "DEVICE LOCATION";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private PendingIntent locationUpdatePendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        buildGoogleApiClient();
        buildPendingIntent();
        createLocationRequest();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(CLASS_ID, "DeviceLocation service started.");

        if (mGoogleApiClient.isConnected() == false)
            mGoogleApiClient.connect();

        return START_STICKY;
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(CLASS_ID, "Connected to Google API.");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationUpdatePendingIntent);
    }

    private void buildPendingIntent() {
        Intent locationUpdateIntent = new Intent(this, LocationUploader.class);
        locationUpdatePendingIntent = PendingIntent.getService(this, 1, locationUpdateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationUpdatePendingIntent);
        mGoogleApiClient.disconnect();
    }
}
