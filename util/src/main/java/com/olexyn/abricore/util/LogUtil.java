package com.olexyn.abricore.util;

import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogUtil {

    public static Logger get(Class c) {
        Logger logger = Logger.getLogger(c.getName());
        try {
            String dir = System.getProperty("user.dir") + "/flow/src/main/resources/main.log";
            FileHandler fh = new FileHandler(dir, true);
            fh.setFormatter(new SimpleFormatter() {
                private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                    );
                }
            });

            logger.addHandler(fh);
            logger.setLevel(Level.INFO);
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }
        return logger;
    }

}
