package com.example.jan.butzradar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Jan on 05.10.2015.
 */
public class LocationEntry {

    public String userid;
    public long timestamp;
    public double latitude;
    public double longitude;
    public int markerColor;
    public float accuracy;

    public LocationEntry(String userid, long timestamp, double latitude, double longitude, int markerColor, float accuracy) {
        this.userid = userid;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.markerColor = markerColor;
        this.accuracy = accuracy;
    }

    public String getPostData() {

        StringBuilder postData = new StringBuilder();

        postData.append(encodeToURIFormat("userid"));
        postData.append("=");
        postData.append(encodeToURIFormat(userid));
        postData.append("&");
        postData.append(encodeToURIFormat("timestamp"));
        postData.append("=");
        postData.append(encodeToURIFormat(String.valueOf(timestamp)));
        postData.append("&");
        postData.append(encodeToURIFormat("latitude"));
        postData.append("=");
        postData.append(encodeToURIFormat(String.valueOf(latitude)));
        postData.append("&");
        postData.append(encodeToURIFormat("longitude"));
        postData.append("=");
        postData.append(encodeToURIFormat(String.valueOf(longitude)));
        postData.append("&");
        postData.append(encodeToURIFormat("markerColor"));
        postData.append("=");
        postData.append(encodeToURIFormat(String.valueOf(markerColor)));
        postData.append("&");
        postData.append(encodeToURIFormat("accuracy"));
        postData.append("=");
        postData.append(encodeToURIFormat(String.valueOf(accuracy)));

        return postData.toString();
    }

    public String encodeToURIFormat(String toEncode) {

        try {
            return URLEncoder.encode(toEncode, "UTF-8");
        } catch(UnsupportedEncodingException uce) {
            return toEncode;
        }
    }
}
