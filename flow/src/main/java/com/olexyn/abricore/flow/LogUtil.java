package com.olexyn.abricore.flow;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUtil {

    public static Logger get(Class c) {
        Logger logger = Logger.getLogger(c.getName());
        try {
            String dir = System.getProperty("user.dir") + "/flow/src/main/resources/main.log";
            FileHandler fh = new FileHandler(dir, true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
            logger.setLevel(Level.INFO);
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        return logger;
    }

}
