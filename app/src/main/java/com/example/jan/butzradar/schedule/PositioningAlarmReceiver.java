package com.example.jan.butzradar.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.example.jan.butzradar.location.DeviceLocation;

public class PositioningAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        acquireWakeLock(context.getApplicationContext());
        context.startService(new Intent(context, DeviceLocation.class));
    }

    private void acquireWakeLock(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "positioningWakeLock");
        wl.acquire();
        RadarliWakeLock.wakeLock = wl;
        Log.i("WAKE_LOCK", "Acquired wakelock");
    }
}
