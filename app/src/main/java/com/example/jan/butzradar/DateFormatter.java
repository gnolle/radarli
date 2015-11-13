package com.example.jan.butzradar;

import java.util.Date;
import java.text.SimpleDateFormat;

public class DateFormatter {

    private static final String DATE_FORMAT_STRING = "dd.MM.yy - HH:mm 'Uhr'";
    private long timestamp;

    public String parsedDate;

    public DateFormatter(long timestamp) {
        this.timestamp = timestamp;
        parseDate();
    }

    private void parseDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
        Date dateToConvert = new Date(timestamp);
        parsedDate = dateFormat.format(dateToConvert);
    }

}
