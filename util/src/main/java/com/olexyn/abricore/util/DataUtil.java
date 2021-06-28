package com.olexyn.abricore.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataUtil {

    private static final DateTimeFormatter SQ_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static LocalDateTime parseDate(String string) {
        return LocalDateTime.parse(string, SQ_DATE_TIME_FORMATTER);
    }

    public static String prettyJson(JSONObject json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = JsonParser.parseString(json.toString());
        return gson.toJson(je);
    }

}
