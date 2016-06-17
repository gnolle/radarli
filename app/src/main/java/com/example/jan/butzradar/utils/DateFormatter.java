package com.example.jan.butzradar.utils;

import java.util.Date;
import java.text.SimpleDateFormat;

public class DateFormatter {

    private static final String DATE_FORMAT_STRING = "dd. MMMM yyyy";
    private static final String TIME_FORMAT_STRING = "HH:mm 'Uhr'";
    private long timestamp;

    public String parsedDate;
    public String parsedTime;

    public DateFormatter(long timestamp) {
        this.timestamp = timestamp;
        parseDate();
    }

    private void parseDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT_STRING);
        Date dateToConvert = new Date(timestamp);
        parsedDate = dateFormat.format(dateToConvert);
        parsedTime = timeFormat.format(dateToConvert);
    }

}
