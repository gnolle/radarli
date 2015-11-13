package com.example.jan.butzradar;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Jan on 08.11.2015.
 */
public class PositioningAlarmReceiver extends WakefulBroadcastReceiver {

    public static final int REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        startWakefulService(context, new Intent(context, DeviceLocationService.class));
    }
}
