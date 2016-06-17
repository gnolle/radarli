package com.example.jan.butzradar.location;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.example.jan.butzradar.schedule.PollAlarmReceiver;

public class LocationPollingScheduler {

    private final static String CLASS_ID = "LOCATION_SCHEDULER";

    private static final int LOCATION_POLLING_FREQUENCY = 60000;

    private Context context;

    public LocationPollingScheduler(Context context) {
        this.context = context;
    }

    public void startPollingAlarm() {

        Log.i(CLASS_ID, "Starting location polling.");

        Intent pollAlarmReceiverIntent = new Intent(context, PollAlarmReceiver.class);

        PendingIntent pollAlarmPendingIntent = PendingIntent.getBroadcast(context, PollAlarmReceiver.REQUEST_CODE, pollAlarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
                LOCATION_POLLING_FREQUENCY, pollAlarmPendingIntent);
    }

    public void cancelLocationPolling() {
        Intent pollAlarmReceiverIntent = new Intent(context, PollAlarmReceiver.class);
        PendingIntent pollAlarmPendingIntent = PendingIntent.getBroadcast(context, PollAlarmReceiver.REQUEST_CODE,
                pollAlarmReceiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pollAlarmPendingIntent);
    }
}
