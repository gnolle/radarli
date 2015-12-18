package com.example.jan.butzradar;

import android.app.IntentService;
import android.content.Intent;

import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderApi;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class LocationUploader extends IntentService {

    private final static String CLASS_ID = "LOCATION_UPLOADER";

    public LocationUploader() {
        super("LocationUploader");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Location location = intent.getParcelableExtra("location");

        Log.i(CLASS_ID, "accuracy: " + location.getAccuracy() + " lat: " + location.getLatitude() + " lon: " + location.getLongitude());

        LocationEntry locationEntry = new LocationEntry(getResources().getString(R.string.user_id), getCurrentTimestamp(), location.getLatitude(), location.getLongitude(), getResources().getInteger(R.integer.marker_color), location.getAccuracy());

        HttpURLConnection urlConnection = null;
        OutputStreamWriter out = null;

        try {

            Log.i(CLASS_ID, "Uploading to server...");

            URL url = new URL(getResources().getString(R.string.server_url));

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);

            String postParameters = locationEntry.getPostData();

            Log.i(CLASS_ID, "POST parameters: " + postParameters);

            urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);

            out = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
            out.write(postParameters);
            out.flush();

        } catch (Exception exc) {
            Log.e(CLASS_ID, "Error uploading location to server. Message: " + exc.getMessage());
        } finally {

            releaseWakelock();
            Log.i("WAKE_LOCK", "Released wakelock");

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.i(CLASS_ID, "Could not close output stream.");
                }
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

        }

    }

    private void releaseWakelock() {
        if (RadarliWakeLock.wakeLock != null) {
            RadarliWakeLock.wakeLock.release();
            RadarliWakeLock.wakeLock = null;
        }
    }

    private long getCurrentTimestamp() {
        Calendar calender = Calendar.getInstance();
        return calender.getTimeInMillis();
    }

}
