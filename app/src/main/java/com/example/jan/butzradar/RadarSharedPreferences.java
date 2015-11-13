package com.example.jan.butzradar;

/**
 * Created by Jan on 09.11.2015.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Camera;
import android.util.Log;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by janklostermann on 09.11.15.
 */
public class RadarSharedPreferences {

private static final String CLASS_ID = "PREFS";
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

    public CameraPosition getLastCameraPos() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        double cameraLatitude = settings.getFloat("camera_latitude", 0);
        double cameraLongitude = settings.getFloat("camera_longitude", 0);
        float cameraBearing = settings.getFloat("camera_bearing", 0);
        float cameraTilt = settings.getFloat("camera_tilt", 0);
        float cameraZoom = settings.getFloat("camera_zoom", 0);

        LatLng cameraLatLng = new LatLng(cameraLatitude, cameraLongitude);
        CameraPosition cameraPosition = new CameraPosition(cameraLatLng, cameraZoom, cameraTilt, cameraBearing);

        return cameraPosition;
    }

    public void setLastCameraPos(CameraPosition cameraPosition) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("camera_pos_set", true);
        editor.putFloat("camera_latitude", (float) cameraPosition.target.latitude);
        editor.putFloat("camera_longitude", (float) cameraPosition.target.longitude);
        editor.putFloat("camera_bearing", cameraPosition.bearing);
        editor.putFloat("camera_tilt", cameraPosition.tilt);
        editor.putFloat("camera_zoom", cameraPosition.zoom);

        editor.commit();
    }

    public boolean isLastCameraPosSet() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        boolean isLastCameraPosSet = settings.getBoolean("camera_pos_set", false);

        return isLastCameraPosSet;
    }

}
