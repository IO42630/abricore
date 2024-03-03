package com.olexyn.abricore.util.num;

import lombok.experimental.UtilityClass;

import static com.olexyn.abricore.util.Constants.DOT;
import static com.olexyn.abricore.util.num.Num.ONE;

/**
 * 9 223 372 036 . 854 775 807
 * Danger zones:
 * 1000 * 1000 * 1000 * 9 = 9 000 000 000
 * 1 / 1000 / 1000 / 1000 = 0.000 000 001
 */
@UtilityClass
public class NumUtil {


    public static String prettyStr(long value, int decimals) {
        if (decimals > 9) { decimals = 9; }

        var valueSb = new StringBuilder(String.valueOf(value));
        while (valueSb.length() < 9) {
            valueSb.insert(0, '0');
        }
        var valueStr = valueSb.toString();

        String numStr = "0";
        if (valueStr.length() > 9) {
            numStr = valueStr.substring(0, valueStr.length() - 9);
        }
        if (decimals <= 0) { return numStr; }


        return numStr + DOT
            + valueStr.substring(
            valueStr.length() - 9,
            valueStr.length() - 9 + decimals
        );
    }


    public static boolean isTrue(long x) {
        return x > 0;
    }

    public static boolean isFalse(long x) {
        return x <= 0;
    }

    public static boolean positive(long x) {
        return x > 0;
    }

    public static long normToOne(long x) {
        if (x == 0) { return 0; }
        return x > 0 ? ONE : Num.N_ONE;
    }

    public static boolean bool(long x) {
        return x > 0;
    }




    public static long fromDouble(double d) {
        return (long) (d * ONE);
    }

    public static long fromInt(int i) {
        return i * ONE;
    }

    public static int toInt(long x) {
        if (x > Integer.MAX_VALUE * ONE) { throw new IllegalArgumentException("Value too large."); }
        if (x < Integer.MIN_VALUE * ONE) { throw new IllegalArgumentException("Value too small."); }
        return (int) (x / ONE);
    }

}
