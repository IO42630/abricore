package com.olexyn.abricore.util.log;

import lombok.Synchronized;

import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import static com.olexyn.abricore.util.Constants.EMPTY;

public class LogFormatter extends SimpleFormatter {

    private static final String FORMAT = "[%1$tF %1$tT] [%2$-7s] [%3$-20s] %4$-120s [%5$s]\n";
    private static final String ROOT_PKG = "com.olexyn.abricore.";
    private static final String LOG_PKG = "com.olexyn.abricore.util.log";


    @Override
    @Synchronized
    public String format(LogRecord logRecord) {
        String msg = logRecord.getMessage();
        return String.format(FORMAT,
            new Date(logRecord.getMillis()),
            logRecord.getLevel().getLocalizedName(),
            jobName(),
            msg,
            methodName()
        );
    }

    public static String jobName() {
        return Thread.currentThread().getName();
    }

    public static String methodName() {
        int pos = 1;
        var callerStack = Thread.currentThread().getStackTrace();
        while (
            callerStack[pos].getClassName().startsWith(LOG_PKG) ||
                !callerStack[pos].getClassName().startsWith(ROOT_PKG)
        ) {
            pos++;
            if (pos > 100) { return EMPTY; }
        }
        return String.format(
            "%s.%s",
            callerStack[pos].getClassName().split(ROOT_PKG)[1],
            callerStack[pos].getMethodName()
        );
    }


}
