package com.example.jan.butzradar;

import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class DetailDialog extends DialogFragment {

    private LocationEntry locationEntry;

    public void setLocationEntry(LocationEntry locationEntry) {
        this.locationEntry = locationEntry;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.layout_markerinfo, null);

        ReverseGeocoder reverseGeocoder = new ReverseGeocoder(getActivity());

        List<Address> addressList = reverseGeocoder.getAddressFromLocation(new LatLng(locationEntry.latitude, locationEntry.longitude));

        if (addressList != null && addressList.size() > 0 && addressList.get(0).getMaxAddressLineIndex() > 1) {
            String addressText = addressList.get(0).getAddressLine(0);
            String addressSubText = addressList.get(0).getAddressLine(1);

            TextView locationTextView = (TextView) dialogView.findViewById(R.id.locationInfo);
            locationTextView.setText(addressText);

            TextView locationSubtextView = (TextView) dialogView.findViewById(R.id.locationInfoSubtext);
            locationSubtextView.setText(addressSubText);
        } else {
            View locationContainer = dialogView.findViewById(R.id.locationContainer);
            locationContainer.setVisibility(View.GONE);
        }


        DateFormatter dateFormatter = new DateFormatter(locationEntry.timestamp);

        TextView timeTextView = (TextView) dialogView.findViewById(R.id.updateText);
        timeTextView.setText(dateFormatter.parsedTime);

        TextView timeSubtextView = (TextView) dialogView.findViewById(R.id.updateSubtext);
        timeSubtextView.setText(dateFormatter.parsedDate);

        builder.setView(dialogView);

        return builder.create();
    }

}
