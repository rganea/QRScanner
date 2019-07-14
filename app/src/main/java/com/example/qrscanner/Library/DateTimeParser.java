package com.example.qrscanner.Library;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeParser {
    static public final long parseDate(String date) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.y");
        Date parsedDate = dateFormat.parse(date);
        // Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
        return parsedDate.getTime();
    }

    static public final String parseTimestamp(long timestamp) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.y");
        return dateFormat.format(new Date(timestamp));
    }

    static public final String parseMillisToMinsAndSecs(long millis){
        long mins = millis / 1000 / 60;
        long secs = millis / 1000 % 60;
        if(secs < 10){
            return String.format("%d:0%d", mins, secs);
        } else {
            return String.format("%d:%d", mins, secs);
        }
    }
}
