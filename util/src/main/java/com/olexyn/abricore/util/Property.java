package com.olexyn.abricore.util;

import com.olexyn.abricore.util.exception.StoreException;
import com.olexyn.abricore.util.log.LogU;
import com.olexyn.abricore.util.num.NumSerialize;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import static com.olexyn.abricore.util.Constants.EMPTY;
import static com.olexyn.abricore.util.Constants.WORKING_DIR;

public final class Property {

    private Property() { }

    private static final Properties config = new Properties();
    private static final Properties events = new Properties();

    /**
     * Initializes the config properties. Must be run before others.
     */
    public static void init() {
        loadProperties(config, "config.properties");
        loadProperties(events, "events.properties");
    }

    public static void loadProperties(Properties properties, String fileName) {
        String dir = get(WORKING_DIR) + "/util/src/main/resources/" + fileName;
        try (var fis = new FileInputStream(dir)) {
            properties.load(fis);
        } catch (IOException e) {
            LogU.warnPlain(e.getMessage(), e);
            System.exit(0);
        }
    }

    /**
     * Fixes where when executing unit tests in module (e.g. flow)
     * the working directory is not set properly (e.g. ../abricore/flow instead of ../abricore).
     */
    private static String fixWorkingDir(String path) {
        String rootPackageName = "abricore";
        if (!path.contains(rootPackageName)) {
            return path.split("ws")[0] + "ws/" + rootPackageName;
        }
        if (!path.endsWith(rootPackageName)) {
            return path.split(rootPackageName)[0] + rootPackageName;
        }
        return path;
    }

    public static void saveProperties(Properties properties, String fileName) {
        String dir = get(WORKING_DIR) + "/util/src/main/resources/" + fileName;
        try (var fos = new FileOutputStream(dir)) {
            properties.store(fos, EMPTY);
            fos.flush();
        } catch (IOException e) {
            LogU.warnPlain(e.getMessage(), e);
        }
        loadProperties(properties, fileName);
    }

    public static boolean is(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static boolean isNot(String key) {
        return !is(key);
    }

    public static Duration getDuration(String key) {
        long amount = Long.parseLong(Property.get(key));
        if (key.contains("days")) {
            return Duration.ofDays(amount);
        }
        if (key.contains("hours")) {
            return Duration.ofHours(amount);
        }
        if (key.contains("minutes")) {
            return Duration.ofMinutes(amount);
        }
        if (key.contains("seconds")) {
            return Duration.ofSeconds(amount);
        }
        return Duration.ofMillis(amount);
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static long getNum(String key) {
        return NumSerialize.fromStr(get(key));
    }

    public static boolean getBool(String key) {
        return Boolean.parseBoolean(get(key));
    }




    public static String get(String key) {
        if (key.equals(WORKING_DIR)) {
            return fixWorkingDir(System.getProperty(key));
        }
        String confProperty = config.getProperty(key);
        if (confProperty != null) { return confProperty; }
        String eventProperty = events.getProperty(key);
        if (eventProperty != null) { return eventProperty; }
        return System.getProperty(key);
    }

    /**
     * Usage: <br>
     * Property.get(WORKING_DIR, "symbols.dir") <br>
     * Property.get("user.dir", "symbols.dir") <br>
     * Property.get("user.dir") + Property.get("symbols.dir") <br>
     * /home/user/ws/idea/abricore + /store/src/main/resources/symbols.json <br>
     */
    public static String get(String... keys) {
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(get(key));
        }
        return sb.toString();
    }

    public static Properties getEvents() {
        return events;
    }
}
