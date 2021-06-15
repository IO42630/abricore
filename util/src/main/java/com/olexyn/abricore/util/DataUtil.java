package com.olexyn.abricore.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataUtil {

    private static final DateTimeFormatter SQ_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static LocalDateTime parseDate(String string) {
        return LocalDateTime.parse(string, SQ_DATE_TIME_FORMATTER);
    }

    public static Double parseDouble(String string){
        string = string.replace("'", "");
        if (string.equals("-")) {
            return 0d;
        }
        return Double.valueOf(string);
    }

    public static Long parseLong(String string){
        string = string.replace("'", "");
        return Long.valueOf(string);
    }
}
