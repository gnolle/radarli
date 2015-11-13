package com.example.jan.butzradar;

import android.app.IntentService;
import android.content.Intent;

import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderApi;


import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

public class LocationUploader extends IntentService {

    private final static String CLASS_ID = "LOCATION UPLOADER";

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

        LocationEntry locationEntry = new LocationEntry(getResources().getString(R.string.user_id), getCurrentTimestamp(), location.getLatitude(), location.getLongitude(), getResources().getInteger(R.integer.marker_color));

        try {

            URL url = new URL(getResources().getString(R.string.server_url));

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postParameters = locationEntry.getPostData();

            Log.i(CLASS_ID, "POST parameters: " + postParameters);

            urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
            out.write(postParameters);
            out.flush();
            out.close();

            urlConnection.disconnect();

        } catch (Exception e) {
            Log.e(CLASS_ID, e.toString());
        }

    }

    private long getCurrentTimestamp() {
        Calendar calender = Calendar.getInstance();
        long timestamp =  calender.getTimeInMillis();
        return timestamp;
    }

}
