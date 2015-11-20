package com.example.jan.butzradar;

//import android.util.Log;

import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by janklostermann on 20.11.15.
 */
public class DistanceCalculator {

    private static final String CLASS_ID = "DISTANCE";

    private void getDistance() {
        try {
            URL url = new URL("https://maps.googleapis.com/maps/api/distancematrix/json?origins=Wolfsburg&destinations=Wernigerode&language=de-DE");
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

    private void parseJSON(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray rows = jsonObject.getJSONArray("rows");
            System.out.println(rows.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new DistanceCalculator().getDistance();
    }
}
