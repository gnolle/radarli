package com.example.jan.butzradar;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

/**
 * Created by Jan on 18.11.2015.
 */
public class ReverseGeocoder {

    private final static String CLASS_ID = "GEOCODER";
    private Geocoder geocoder;
    private List<Address> addresses;

    public ReverseGeocoder(Context context) {
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    public void getAddressFromLocation(LatLng location) {
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            Log.i(CLASS_ID, addresses.get(0).toString());
        } catch (Exception e) {
            Log.e(CLASS_ID, e.getMessage());
        }
    }
}
