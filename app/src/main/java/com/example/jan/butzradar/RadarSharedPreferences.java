package com.example.jan.butzradar;

/**
 * Created by Jan on 09.11.2015.
 */
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by janklostermann on 09.11.15.
 */
public class RadarSharedPreferences {

    private static final String PREFS_NAME = "RadarliPrefs";
    private Context context;

    public RadarSharedPreferences(Context context) {
        this.context = context;
    }

    public boolean getPositioningSetting() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        boolean positioningSetting = settings.getBoolean("positioning", true);

        return positioningSetting;
    }

    public void setPositioningSetting(boolean positioningSetting) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("positioning", positioningSetting);

        editor.commit();
    }

}
