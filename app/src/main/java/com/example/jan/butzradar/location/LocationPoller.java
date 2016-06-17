package com.example.jan.butzradar.location;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationPoller extends IntentService {

    private final static String CLASS_ID = "LOCATION POLLER";

    public LocationPoller() {
        super("LocationPoller");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(CLASS_ID, "Location poller started");
        startDownloading();
    }

    private void startDownloading() {

        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        try {
            URL url = new URL("http://www.lolnice.de/butzi_finder/locations.json");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);

            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String readLine;

            StringBuilder response = new StringBuilder();
            while ((readLine = bufferedReader.readLine()) != null) {
                response.append(readLine);
            }

            Log.i(CLASS_ID, response.toString());

            broadcastNewServerLocations(response.toString());

        }
        catch (Exception exc) {
            Log.e(CLASS_ID, "Error polling server.");
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.i(CLASS_ID, "Could not close buffered reader.");
                }
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private void broadcastNewServerLocations(String newServerLocations) {
        Intent intent = new Intent("updated-server-locations");

        intent.putExtra("locations", newServerLocations);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

