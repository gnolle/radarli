package com.example.jan.butzradar;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
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
        try {
            URL url = new URL("http://www.lolnice.de/butzi_finder/locations.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection", "close");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String readLine;

            StringBuilder response = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                response.append(readLine);
            }

            Log.i(CLASS_ID, response.toString());

            broadcastNewServerLocations(response.toString());

            br.close();
            connection.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastNewServerLocations(String newServerLocations) {
        Intent intent = new Intent("updated-server-locations");

        intent.putExtra("locations", newServerLocations);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

