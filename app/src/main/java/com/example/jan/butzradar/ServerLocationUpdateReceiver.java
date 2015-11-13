package com.example.jan.butzradar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class ServerLocationUpdateReceiver extends BroadcastReceiver {

    private final static String CLASS_ID = "SERVER_UPDATE_RECEIVER";

    private ButzRadar context;
    private JSONParser jsonParser;

    public ServerLocationUpdateReceiver(Context context) {
        this.context = (ButzRadar) context;

        registerServerLocationReceiver();
    }

    private void registerServerLocationReceiver() {
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter("updated-server-locations"));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        handleServerLocationUpdate(intent);
    }

    private void handleServerLocationUpdate(Intent intent) {
        String newServerLocations = intent.getStringExtra("locations");
        Log.i(CLASS_ID, "Received new locations: " + newServerLocations);

        jsonParser = new JSONParser(newServerLocations);

        context.initiateMarkerReplace(jsonParser.entries);
    }


}
