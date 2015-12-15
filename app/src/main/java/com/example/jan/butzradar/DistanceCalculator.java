package com.example.jan.butzradar;

//import android.util.Log;

import android.location.Address;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by janklostermann on 20.11.15.
 */
public class DistanceCalculator extends AsyncTask<Void, Void, DistanceResult> {

    private static final String CLASS_ID = "DISTANCE";
    private LatLng origin;
    private LatLng destination;
    private String serverResponse;
    private DistanceResult distanceResult;
    private DetailDialog detailDialog;

    public DistanceCalculator(LatLng origin, LatLng destination, DetailDialog detailDialog) {
        super();

        this.origin = origin;
        this.destination = destination;
        this.detailDialog = detailDialog;
    }

    @Override
    protected DistanceResult doInBackground(Void... params) {
        getDistance();
        parseJSON();

        return distanceResult;
    }

    @Override
    protected void onPostExecute(DistanceResult distanceResult) {
        if (distanceResult != null)
            detailDialog.showDistanceResult(distanceResult);
    }

    private void getDistance() {

        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        try {
            URL url = new URL(buildAPICall());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);

            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String readLine;

            StringBuilder response = new StringBuilder();
            while ((readLine = bufferedReader.readLine()) != null) {
                response.append(readLine);
            }

            serverResponse = response.toString();

            Log.i(CLASS_ID, serverResponse);

        }
        catch (Exception exc) {
           Log.e(CLASS_ID, exc.getMessage());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.i(CLASS_ID, "Could not close buffered reader.");
                }
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private String buildAPICall() {
        StringBuilder callURL = new StringBuilder();
        callURL.append("https://maps.googleapis.com/maps/api/distancematrix/json?origins=");
        callURL.append(String.valueOf(origin.latitude) + "," + String.valueOf(origin.longitude));
        callURL.append("&destinations=");
        callURL.append(String.valueOf(destination.latitude) + "," + String.valueOf(destination.longitude));
        callURL.append("&language=de-DE");
        callURL.append("&departure_time=now");
        callURL.append("&key=" + detailDialog.getResources().getString(R.string.gmaps_server_key));

        return callURL.toString();
    }

    private void parseJSON() {
        try {
            JSONObject jsonObject = new JSONObject(serverResponse);
            JSONArray rows = jsonObject.getJSONArray("rows");

            JSONObject firstRow = rows.getJSONObject(0);

            JSONArray elements = firstRow.getJSONArray("elements");

            JSONObject firstElement = elements.getJSONObject(0);

            JSONObject distance = firstElement.getJSONObject("distance");
            String distanceText = distance.getString("text");

            JSONObject duration = firstElement.getJSONObject("duration_in_traffic");
            String durationText = duration.getString("text");

            distanceResult = new DistanceResult(distanceText, durationText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
