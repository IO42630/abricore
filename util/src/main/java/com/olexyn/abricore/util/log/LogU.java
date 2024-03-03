package com.olexyn.abricore.util.log;

import com.olexyn.propconf.PropConf;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.olexyn.abricore.util.log.LogPrint.END;
import static com.olexyn.abricore.util.log.LogPrint.LOAD;
import static com.olexyn.abricore.util.log.LogPrint.PLAIN;
import static com.olexyn.abricore.util.log.LogPrint.SAVE;
import static com.olexyn.abricore.util.log.LogPrint.START;
import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

public final class LogU {

    private LogU() { }





    private static Set<String> ignores = null;

    static {
        String raw = PropConf.get("threads.prefix.to.ignore.below.info");
        ignores = Arrays.stream(raw.split(":")).collect(Collectors.toSet());
    }



    private static final Logger logger = make();

    public static Logger make() {
        var logger = Logger.getLogger("default");
        logger.setUseParentHandlers(false);
        var ch = new ConsoleHandler();
        ch.setFormatter(new LogFormatter());
        logger.addHandler(ch);

        String dir = PropConf.get("logs.dir") + "main.log";
        try {
            var fh = new FileHandler(dir, true);
            fh.setFormatter(new LogFormatter());
            logger.addHandler(fh);
        } catch (IOException e) { LogU.warnPlain(e.getMessage(), e); }
        logger.setLevel(Level.INFO);
        return logger;
    }



    private static String commonMsg(String bracket, String msg, @Nullable Object... args) {
        if (args != null) { msg = String.format(msg, args); }
        return String.format(bracket + "   %-80s", msg);
    }

    public static void log(Level level, LogPrint logPrint, @NonNull String msg, @Nullable Object... args) {

        boolean isIgnored = ignores.contains(LogFormatter.jobName().toUpperCase());
        if (isIgnored && level.intValue() < INFO.intValue()) {
            return;
        }
        switch (logPrint) {
            case START -> msg = commonMsg("[START >    ]", msg, args);
            case END -> msg = commonMsg("[      > END]", msg, args);
            case PLAIN -> msg = commonMsg("[           ]", msg, args);
            case SAVE -> msg = commonMsg("[       SAVE]", msg, args);
            case LOAD -> msg = commonMsg("[LOAD       ]", msg, args);
            default -> { }
        }
        logger.log(level, msg);
    }

    public static void fineStart(@NonNull String msg, @Nullable Object... args) {
        log(FINE, START, msg, args);
    }

    public static void fineEnd(@NonNull String msg, @Nullable Object... args) {
        log(FINE, END, msg, args);
    }


    public static void finePlain(@NonNull String msg, @Nullable Object... args) {
        log(FINE, PLAIN, msg, args);
    }

    public static void infoStart(@NonNull String msg, @Nullable Object... args) {
        log(INFO, START, msg, args);
    }


    public static void infoEnd(@NonNull String msg, @Nullable Object... args) {
        log(INFO, END, msg, args);
    }


    public static void infoPlain(@NonNull String msg, @Nullable Object... args) {
        log(INFO, PLAIN, msg, args);
    }

    public static void warnStart(@NonNull String msg, @Nullable Object... args) {
        log(WARNING, START, msg, args);
    }

    public static void warnEnd(@NonNull String msg, @Nullable Object... args) {
        log(WARNING, END, msg, args);
    }

    public static void warnPlain(@NonNull String msg, @Nullable Object... args) {
        log(WARNING, PLAIN, msg, args);
    }

    public static void save(@NonNull String msg, @Nullable Object... args) {
        log(INFO, SAVE, msg, args);
    }

    public static void load(@NonNull String msg, @Nullable Object... args) {
        log(INFO, LOAD, msg, args);
    }



}
