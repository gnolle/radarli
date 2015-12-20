package com.example.jan.butzradar;

import android.app.Dialog;
import android.location.Address;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class DetailDialog extends DialogFragment {

    private LocationEntry locationEntry;
    private View dialogView;

    public void setLocationEntry(LocationEntry locationEntry) {
        this.locationEntry = locationEntry;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        dialogView = inflater.inflate(R.layout.layout_markerinfo, null);

        hideDynamicPanels();

        showTimePanel();

        startGeocoding();

        startDistanceCalculator();

        builder.setView(dialogView);

        return builder.create();
    }

    private void showTimePanel() {
        DateFormatter dateFormatter = new DateFormatter(locationEntry.timestamp);

        TextView timeTextView = (TextView) dialogView.findViewById(R.id.updateText);
        timeTextView.setText(dateFormatter.parsedTime);

        TextView timeSubtextView = (TextView) dialogView.findViewById(R.id.updateSubtext);
        timeSubtextView.setText(dateFormatter.parsedDate);
    }

    private void startGeocoding() {
        ReverseGeocoder reverseGeocoder = new ReverseGeocoder(getActivity(), this);
        reverseGeocoder.execute(new LatLng(locationEntry.latitude, locationEntry.longitude));
    }

    private void startDistanceCalculator() {
        RadarSharedPreferences radarSharedPreferences = new RadarSharedPreferences(getActivity());

        DistanceCalculator distanceCalculator = new DistanceCalculator(radarSharedPreferences.getOwnLocation(), new LatLng(locationEntry.latitude, locationEntry.longitude), this);
        distanceCalculator.execute();
    }

    public void showGeocodingResult(List<Address> addressList) {
        if (addressList != null && addressList.size() > 0 && addressList.get(0).getMaxAddressLineIndex() > 0) {
            String addressText = addressList.get(0).getAddressLine(0);
            String addressSubText = addressList.get(0).getAddressLine(1);

            TextView locationTextView = (TextView) dialogView.findViewById(R.id.locationInfo);
            locationTextView.setText(addressText);

            TextView locationSubtextView = (TextView) dialogView.findViewById(R.id.locationInfoSubtext);
            locationSubtextView.setText(addressSubText);

            showGeocodingPanel();
        }
    }

    public void showDistanceResult(DistanceResult distanceResult) {

        TextView travelText = (TextView) dialogView.findViewById(R.id.travelText);
        travelText.setText(distanceResult.travelTime);

        TextView travelSubtext = (TextView) dialogView.findViewById(R.id.travelSubtext);
        travelSubtext.setText(distanceResult.distance);

        showDistancePanel();
    }

    private void showGeocodingPanel() {
        View geocoderContainer = dialogView.findViewById(R.id.geocoderContainer);
        geocoderContainer.setVisibility(View.VISIBLE);
    }

    private void showDistancePanel() {
        View distanceContainer = dialogView.findViewById(R.id.distanceContainer);
        distanceContainer.setVisibility(View.VISIBLE);
    }

    private void hideDynamicPanels() {
        View geocoderContainer = dialogView.findViewById(R.id.geocoderContainer);
        geocoderContainer.setVisibility(View.GONE);

        View distanceContainer = dialogView.findViewById(R.id.distanceContainer);
        distanceContainer.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        dismiss();
    }
}
