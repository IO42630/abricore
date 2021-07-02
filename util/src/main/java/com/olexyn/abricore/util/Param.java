package com.olexyn.abricore.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Param {

    public static final Properties config = new Properties();
    public static final Properties events = new Properties();

    public static void init() throws IOException {
        loadProperties(config, "config.properties");
        loadProperties(events, "events.properties");
    }

    public static void loadProperties(Properties properties, String fileName) throws IOException {
        String dir = System.getProperty("user.dir") + "/util/src/main/resources/" + fileName;
        FileInputStream fis = new FileInputStream(dir);
        properties.load(fis);
        fis.close();
    }

    public static void saveProperties(Properties properties, String fileName) throws IOException {
        String dir = System.getProperty("user.dir") + "/util/src/main/resources/" + fileName;
        FileOutputStream fos = new FileOutputStream(dir);
        properties.store(fos, "");
        fos.flush();
        fos.close();
        loadProperties(properties, fileName);
    }

    public static boolean isEnabled(String prop) {
        return Boolean.parseBoolean(config.getProperty(prop));
    }

}
