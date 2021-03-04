package com.olexyn.abricore.util;

import static com.olexyn.abricore.util.Constants.*;

public class Calc {

    public final static int PRECISION = 3;
    public final static int MAX_VALUE = 3000001;

    public static String parseString(Long l) {
        if (l == null) {
            return NULL;
        }
        String full = l.toString();
        String whole = full.substring(0, full.length() - PRECISION);
        String floating = full.substring(full.length() - PRECISION);
        return whole + DOT+ floating;
    }

    public static Long parseLong(String s) {
        if (s.equals(NULL)) {
            return null;
        }
        double d = Double.parseDouble(s);
        d = d * Math.pow(10, PRECISION);
        return Math.round(d);
    }


    public static Long multiply(Long... values) {
        long out = 1000L;
        for (Long value : values) {
            out = (out *value) / 1000;
        }
        return out;
    }
}
