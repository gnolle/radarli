package com.example.jan.butzradar;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReverseGeocoder extends AsyncTask<LatLng, Void, List<Address>> {

    private final static String CLASS_ID = "GEOCODER";
    private DetailDialog detailDialog;
    private Geocoder geocoder;

    public ReverseGeocoder(Context context, DetailDialog detailDialog) {
        this.detailDialog = detailDialog;
        geocoder = new Geocoder(context, Locale.getDefault());
    }

    @Override
    protected List<Address> doInBackground(LatLng... positions) {
        List<Address> addresses = new ArrayList<>();
        try {
            addresses = geocoder.getFromLocation(positions[0].latitude, positions[0].longitude, 1);
            Log.i(CLASS_ID, addresses.get(0).toString());
        } catch (Exception e) {
            Log.e(CLASS_ID, e.getMessage());
        }
        return addresses;
    }

    @Override
    protected void onPostExecute(List<Address> addressList) {
        detailDialog.showGeocodingResult(addressList);
    }
}
