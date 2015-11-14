package com.example.jan.butzradar;

import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackBarBuilder {

    public static void showSnackBar(String snackBarText, View view) {
        Snackbar.make(view, snackBarText, Snackbar.LENGTH_LONG)
                .show();
    }

}
