package com.example.jan.butzradar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class PositioningScheduler {

    private final static String CLASS_ID = "POSITIONING_SCHEDULER";

    private static final int POSITIONING_FREQUENCY = 120000;

    private Context context;

    public PositioningScheduler(Context context) {
        this.context = context;
    }

    public void startPositioningAlarm() {

        Log.i(CLASS_ID, "Starting positioning");

        Intent positioningAlarmReceiverIntent = new Intent(context, PositioningAlarmReceiver.class);

        PendingIntent positioningAlarmPendingIntent = PendingIntent.getBroadcast(context, PositioningAlarmReceiver.REQUEST_CODE, positioningAlarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                POSITIONING_FREQUENCY, positioningAlarmPendingIntent);
    }

    public void cancelPositioning() {
        Intent positioningAlarmReceiverIntent = new Intent(context, PositioningAlarmReceiver.class);
        PendingIntent positioningAlarmPendingIntent = PendingIntent.getBroadcast(context, PositioningAlarmReceiver.REQUEST_CODE,
                positioningAlarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(positioningAlarmPendingIntent);
    }

}
