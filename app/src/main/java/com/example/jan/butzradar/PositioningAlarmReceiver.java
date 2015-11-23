package com.example.jan.butzradar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class PositioningAlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        acquireWakeLock(context);
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
