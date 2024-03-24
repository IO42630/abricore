package com.olexyn.abricore.navi.mwatch;

import com.olexyn.abricore.navi.sq.SqSession;
import com.olexyn.abricore.navi.tw.TwSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * MWatch is a static class that keeps track of the state of the MWatchable classes.
 * It is used to prevent multiple instances of the same class from running at the same time.
 * This is necessary because the MWatchable classes are not thread safe.
 * The MWatchable classes are not thread safe because they use the same TabDriver instance.
 */
public final class MWatch {

    private static final Map<Class<? extends MWatchable>, Boolean> map = new HashMap<>();

    static {
        map.put(TwSession.class, false);
        map.put(SqSession.class, false);
    }
    private MWatch() { }

    public static boolean isAlive(Class<? extends MWatchable> clazz) {
        return Optional.ofNullable(map.get(clazz)).orElse(false);
    }

    public static boolean isDead(Class<? extends MWatchable> clazz) {
        return !isAlive(clazz);
    }

    public static void setAlive(Class<? extends MWatchable> clazz) {
        map.put(clazz, true);
    }

    public static void setDead(Class<? extends MWatchable> clazz) {
        map.put(clazz, false);
    }

}
