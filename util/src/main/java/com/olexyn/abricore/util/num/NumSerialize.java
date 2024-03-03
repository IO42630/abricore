package com.olexyn.abricore.util.num;

import lombok.experimental.UtilityClass;

import static com.olexyn.abricore.util.Constants.DASH;
import static com.olexyn.abricore.util.Constants.DOT;
import static com.olexyn.abricore.util.Constants.EMPTY;

@UtilityClass
public class NumSerialize {

    private static final String ZERO_SERIALIZE = "0.000000000";


    public static String toStr(long value) {
        if (value == 0) { return ZERO_SERIALIZE; }
        var fullSb = new StringBuilder(String.valueOf(value));
        String sign = EMPTY;
        if (fullSb.charAt(0) == '-') {
            sign = DASH;
            fullSb.deleteCharAt(0);
        }
        while (fullSb.length() < 10) {
            fullSb.insert(0, '0');
        }
        return sign
            + fullSb.substring(0, fullSb.length() - 9)
            + DOT
            + fullSb.substring(fullSb.length() - 9);
    }

    public static long fromStr(String str) {
        if (str == null
            || ZERO_SERIALIZE.equals(str)
            || str.isEmpty()
            || str.equals("0")
            || str.equals("null")
            || str.equals("NAN")
        ) { return 0; }
        int sign = 1;
        if (str.charAt(0) == '-') {
            sign = -1;
            str = str.substring(1);
        }
        var split = str.split("\\.");
        try {
            long num = Long.parseLong(split[0]) * Num.ONE;
            if (split.length == 1) { return sign * num; }
            StringBuilder decSb = new StringBuilder(split[1]);
            while (decSb.length() < 9) {
                decSb.append('0');
            }
            long dec = Long.parseLong(decSb.substring(0, 9));
            return sign * (num + dec);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
