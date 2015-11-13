package com.example.jan.butzradar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Jan on 06.10.2015.
 */
public class PollAlarmReceiver extends BroadcastReceiver {

    private final static String CLASS_ID = "ALARM_RECEIVER";

    public static final int REQUEST_CODE = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(CLASS_ID, "Alarm tick received");

        Intent startLocationPoll = new Intent(context, LocationPoller.class);
        context.startService(startLocationPoll);
    }
}
