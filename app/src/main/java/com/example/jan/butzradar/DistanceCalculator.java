package com.example.jan.butzradar;

//import android.util.Log;

import android.util.JsonReader;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by janklostermann on 20.11.15.
 */
public class DistanceCalculator {

    private static final String CLASS_ID = "DISTANCE";
    private LatLng origin;
    private LatLng destination;

    public DistanceCalculator(LatLng origin, LatLng destination) {
        this.origin = origin;
        this.destination = destination;
    }

    private void getDistance() {
        try {
            URL url = new URL(buildAPICall());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection", "close");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String readLine;

            StringBuilder response = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                response.append(readLine);
            }

           // Log.i(CLASS_ID, response.toString());

            System.out.println(response.toString());

            parseJSON(response.toString());

            br.close();
            connection.disconnect();
        }
        catch (Exception exc) {
           // Log.e(CLASS_ID, "Error calculating distance.");
        }
    }

    private String buildAPICall() {
        StringBuilder callURL = new StringBuilder();
        callURL.append(encodeToURIFormat("https://maps.googleapis.com/maps/api/distancematrix/json?origins="));
        callURL.append(encodeToURIFormat(String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude)));
        callURL.append(encodeToURIFormat("&destinations="));
        callURL.append(encodeToURIFormat(String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude)));
        callURL.append(encodeToURIFormat("&language=de-DE"));

        return callURL.toString();
    }

    public String encodeToURIFormat(String toEncode) {

        try {
            return URLEncoder.encode(toEncode, "UTF-8");
        } catch(UnsupportedEncodingException uce) {
            return toEncode;
        }
    }

    private void parseJSON(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray rows = jsonObject.getJSONArray("rows");
            System.out.println(rows.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
