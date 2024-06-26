package com.olexyn.abricore.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.olexyn.propconf.PropConf;
import lombok.experimental.UtilityClass;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public final class DataUtil {

    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Zurich");

    public static final DateTimeFormatter SQ_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    public static final DateTimeFormatter SQ_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter SQ_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static LocalDateTime parseDateTime(String string) {
        return LocalDateTime.parse(string, SQ_DATE_TIME_FORMATTER);
    }

    public static LocalDate parseDate(String string) {
        return LocalDate.parse(string, SQ_DATE_FORMATTER);
    }

    public static LocalTime parseTime(String string) {
        return LocalTime.parse(string, SQ_TIME_FORMATTER);
    }


    public static Instant getInstant(LocalDate date, LocalTime time) {
        return Instant.from(ZonedDateTime.of(date, time, DEFAULT_ZONE));
    }

    public static LocalDate getLocalDate(Instant instant) {
        return instant.atZone(DEFAULT_ZONE).toLocalDate();
    }

    public static Instant getInstantSoDay(LocalDate date) {
        LocalDateTime dateTime = date.atStartOfDay();
        return Instant.from(ZonedDateTime.of(dateTime, DEFAULT_ZONE));
    }

    public static Instant getInstantEoDay(LocalDate date) {
        LocalDateTime dateTime = date.atTime(23, 59, 59);
        return Instant.from(ZonedDateTime.of(dateTime, DEFAULT_ZONE));
    }

    public static Instant getInstant(LocalDateTime dateTime) {
        return Instant.from(ZonedDateTime.of(dateTime, DEFAULT_ZONE));
    }

    public static LocalDateTime getLocalDateTime(Instant instant) {
        return instant.atZone(DEFAULT_ZONE).toLocalDateTime();
    }

    public static Instant getInstant(String dateTimeProperty) {
        var dateTime = parseDateTime(PropConf.get(dateTimeProperty));
        return getInstant(dateTime);
    }

    public static String prettyJson(JSONObject json) {
        return prettyJson(json.toString());
    }

    public static String prettyJson(String json) {
        JsonElement je = JsonParser.parseString(json);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(je);
    }

    public static Map<String, String> resolveHrefParams(String href) {
        href = href.substring(href.indexOf('?') + 1);
        String[] params = href.split("&");
        Map<String, String> paramMap = new HashMap<>();
        for (String param : params) {
            String[] paramEntry = param.split("=");
            paramMap.put(paramEntry[0], paramEntry[1]);
        }
        return paramMap;
    }

}
