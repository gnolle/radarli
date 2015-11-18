package com.example.jan.butzradar;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jan on 01.10.2015.
 */
public class MapViewer implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private final static String CLASS_ID = "MAP_VIEWER";
    private static final int MAP_MARKER_PADDING = 150;
    private static final LatLng GERMANY_GEOGRAPHIC_CENTER = new LatLng(51.5167, 10.5);
    private static final float INITIAL_ZOOM_LEVEL = 5.5f;
    private static final float FOCUS_ZOOM_LEVEL = 17;

    private ButzRadar context;
    private int markerFocusIndex;

    private MapFragment mapFragment;
    private GoogleMap googleMap;
    private List<Marker> positionMarkers;
    private List<Circle> accuracyCircles;
    private HashMap<String, LocationEntry> markerMap;

    public MapViewer(Context context) {
        this.context = (ButzRadar) context;
        positionMarkers = new ArrayList<>();
        accuracyCircles = new ArrayList<>();
        markerMap = new HashMap<>();

        createMap();
    }


    private void createMap() {
        mapFragment = (MapFragment) context.getFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
        googleMap = mapFragment.getMap();
        googleMap.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        LocationEntry locationEntry;
        try {
            locationEntry = getLocationEntryForMarker(marker);
        } catch (Exception e) {
            return true;
        }

        DateFormatter dateFormatter = new DateFormatter(locationEntry.timestamp);

        StringBuilder snackBarText = new StringBuilder();

        snackBarText.append("Letztes Update: ");
        snackBarText.append(dateFormatter.parsedDate);

        SnackBarBuilder.showSnackBar(snackBarText.toString(), context.findViewById(R.id.container));

        return true;
    }

    private LocationEntry getLocationEntryForMarker(Marker marker) throws Exception {
        String markerID = marker.getId();

        if (markerMap.containsKey(markerID) == false)
            throw new Exception("Marker ID not found");

        LocationEntry locationEntry = markerMap.get(markerID);

        return locationEntry;
    }


    @Override
    public void onMapReady(GoogleMap map) {

        if (context.radarSharedPreferences.isLastCameraPosSet()) {
            focusCameraOnCameraPosition(context.radarSharedPreferences.getLastCameraPos());
        } else {
            initMapOnGermany();
        }

        context.startLocationPolling();
    }

    private void initMapOnGermany() {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GERMANY_GEOGRAPHIC_CENTER, INITIAL_ZOOM_LEVEL));
    }


    public void replaceLocationMarkers(LocationEntry[] locationEntries) {
        Log.i(CLASS_ID, "Received new locations.");

        deleteMarkers();

        createPositionMarkers(locationEntries);

    }

    private void createPositionMarkers(LocationEntry[] locationEntries) {

        for (LocationEntry locationEntry : locationEntries) {
                createPositionMarker(locationEntry);
        }
    }


    private void createPositionMarker(LocationEntry locationEntry) {

        Log.i(CLASS_ID, "Replacing marker");

        BitmapDescriptor markerIcon;

        switch(locationEntry.markerColor) {
            case 0:
                markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_pink);
                break;
            case 1:
                markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_blue);
                break;
            default:
                markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker_pink);
                break;
        }

        LatLng markerPosition = new LatLng(locationEntry.latitude, locationEntry.longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(markerPosition).icon(markerIcon);
        Marker marker = googleMap.addMarker(markerOptions);

        drawAccuracyCircle(locationEntry);

        positionMarkers.add(marker);

        markerMap.put(marker.getId(), locationEntry);

    }

    private void drawAccuracyCircle(LocationEntry locationEntry) {

        CircleOptions circleOptions = new CircleOptions();

        circleOptions
                .radius(locationEntry.accuracy)
                .center(new LatLng(locationEntry.latitude, locationEntry.longitude))
                .strokeWidth(3);

        switch (locationEntry.markerColor) {
            case 0:
                circleOptions
                        .strokeColor(ContextCompat.getColor(context, R.color.pink_circle_stroke))
                        .fillColor(ContextCompat.getColor(context, R.color.pink_circle_fill));
                break;
            case 1:
                circleOptions
                        .strokeColor(ContextCompat.getColor(context, R.color.blue_circle_stroke))
                        .fillColor(ContextCompat.getColor(context, R.color.blue_circle_fill));
                break;
            default:
                circleOptions
                        .strokeColor(ContextCompat.getColor(context, R.color.pink_circle_stroke))
                        .fillColor(ContextCompat.getColor(context, R.color.pink_circle_fill));
                break;
        }

        Circle circle = googleMap.addCircle(circleOptions);

        accuracyCircles.add(circle);
    }

    private void deleteMarkers() {
        for (Marker marker : positionMarkers) {
           marker.remove();
        }

        for (Circle circle : accuracyCircles) {
            circle.remove();
        }

        positionMarkers.clear();
        accuracyCircles.clear();
        markerMap.clear();
    }


    public void changeCameraToBounds() {

        if (positionMarkers.size() == 0)
            return;

        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

        for (Marker marker : positionMarkers) {
            latLngBuilder.include(marker.getPosition());
        }

        LatLngBounds latLngBounds = latLngBuilder.build();

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, MAP_MARKER_PADDING));
    }

    public void focusCameraOnMarker() {
        if (positionMarkers == null || positionMarkers.size() == 0)
            return;

        if (markerFocusIndex > positionMarkers.size() - 1)
            markerFocusIndex = 0;

        Marker focusedMarker = positionMarkers.get(markerFocusIndex);
        LatLng focusedMarkerPosition = focusedMarker.getPosition();

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(focusedMarkerPosition, FOCUS_ZOOM_LEVEL));

        markerFocusIndex++;
    }

    public void focusCameraOnCameraPosition(CameraPosition cameraPosition) {
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public CameraPosition getCameraPos() {
         return googleMap.getCameraPosition();
    }

}
