package com.olexyn.abricore.util.num;

import com.olexyn.abricore.util.exception.SoftCalcException;
import lombok.experimental.UtilityClass;

import static com.olexyn.abricore.util.Constants.RNG;
import static com.olexyn.abricore.util.num.Num.EN1;
import static com.olexyn.abricore.util.num.Num.EN2;
import static com.olexyn.abricore.util.num.Num.EN3;
import static com.olexyn.abricore.util.num.Num.EN4;
import static com.olexyn.abricore.util.num.Num.EN5;
import static com.olexyn.abricore.util.num.Num.EN6;
import static com.olexyn.abricore.util.num.Num.EN7;
import static com.olexyn.abricore.util.num.Num.EN8;
import static com.olexyn.abricore.util.num.Num.EN9;
import static com.olexyn.abricore.util.num.Num.EP0;
import static com.olexyn.abricore.util.num.Num.EP1;
import static com.olexyn.abricore.util.num.Num.EP2;
import static com.olexyn.abricore.util.num.Num.EP3;
import static com.olexyn.abricore.util.num.Num.EP4;
import static com.olexyn.abricore.util.num.Num.EP5;
import static com.olexyn.abricore.util.num.Num.EP6;
import static com.olexyn.abricore.util.num.Num.EP7;
import static com.olexyn.abricore.util.num.Num.EP8;
import static com.olexyn.abricore.util.num.Num.EP9;
import static com.olexyn.abricore.util.num.Num.NINE_EP7;
import static com.olexyn.abricore.util.num.Num.ONE;
import static com.olexyn.abricore.util.num.Num.TEN;
import static com.olexyn.abricore.util.num.Num.TWO;
import static com.olexyn.abricore.util.num.NumSerialize.toStr;
import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;

/**
 * 9 223 372 036 . 854 775 807
 * Danger zones:
 * 1000 * 1000 * 1000 * 9 = 9 000 000 000
 * 1 / 1000 / 1000 / 1000 = 0.000 000 001
 */
@UtilityClass
public class NumCalc {

    private static final String TIMES_OVERFLOW = "Times overflow.";

    /**
     * The closer the numbers together, the larger the loss of precision.
     * 5 / 5
     * 5KKK  / 5KKK
     * 5KKK  * 1K / 5KKK * 1KKK / 1K
     * 5K         / 5    * 1KKK / 1K
     * loss: 0.001
     * <p>
     * 50 / 5
     * 50KKK / 5KKK
     * 50KKK * 1K / 5KKK * 1KKK / 1K
     * 50K        / 5    * 1KKK / 1K
     * loss: 0.0001
     * <p>
     * 5 / 50
     * 5KKK / 50KKK
     * 5KKK * 1KKK / 50KKK -> 1KKK / 10 -> 100
     */
    public static long div(long dividend, long divider) {
        if (divider == 0) { throw new SoftCalcException("DIV by 0."); }
        long abs = abs(dividend);
        if (abs < EN8) { return dividend * EP8 / divider / EN1; }
        if (abs < EN7) { return dividend * EP7 / divider / EN2; }
        if (abs < EN6) { return dividend * EP6 / divider / EN3; }
        if (abs < EN5) { return dividend * EP5 / divider / EN4; }
        if (abs < EN4) { return dividend * EP4 / divider / EN5; }
        if (abs < EN3) { return dividend * EP3 / divider / EN6; }
        if (abs < EN2) { return dividend * EP2 / divider / EN7; }
        if (abs < EN1) { return dividend * TEN / divider / EN8; }
        if (abs < ONE) { return dividend * ONE / divider; }
        if (abs < TEN) { return dividend * EN1 / divider * EN8; }
        if (abs < EP2) { return dividend * EN2 / divider * EN7; }
        if (abs < EP3) { return dividend * EN3 / divider * EN6; }
        if (abs < EP4) { return dividend * EN4 / divider * EN5; }
        if (abs < EP5) { return dividend * EN5 / divider * EN4; }
        if (abs < EP6) { return dividend * EN6 / divider * EN3; }
        if (abs < EP7) { return dividend * EN7 / divider * EN2; }
        if (abs < EP8) { return dividend * EN8 / divider * EN1; }
        if (abs < EP9) { return dividend * EN9 / divider * ONE; }
        return dividend / 10 / divider * TEN;
    }

    public static long times(long... values) {
        long result = ONE;
        for (long y : values) {
            if (y > NINE_EP7) { throw new SoftCalcException(TIMES_OVERFLOW); }
            long abs = abs(result);
            if (abs < EN8) {
                result = result * EN7 * y / EP2;
            } else if (abs < EN7) {
                result = result * EN8 * y / EP1;
            } else if (abs < EN6) {
                result = result * y / EP0;
            } else if (abs < EN5) {
                result = result / EN8 * y / EN1;
            } else if (abs < EN4) {
                result = result / EN7 * y / EN2;
            } else if (abs < EN3) {
                result = result / EN6 * y / EN3;
            } else if (abs < EN2) {
                result = result / EN5 * y / EN4;
            } else if (abs < EN1) {
                result = result / EN4 * y / EN5;
            } else if (abs < EP0) {
                result = result / EN3 * y / EN6;
            } else if (abs < EP1) {
                result = result / EN2 * y / EN7;
            } else if (abs < EP2) {
                result = result / EN1 * y / EN8;
            } else {
                result = result / ONE * y;
            }
        }
        return result;
    }

    public static long num(long x) {
        return x / ONE * ONE;
    }

    public static long round(long x, long precision) {
        return (x + div(precision, TWO)) / precision * precision;
    }

    public static long rng() {
        return RNG.nextLong(MIN_VALUE, MAX_VALUE);
    }

    public static long rng(long min, long max) {
        return RNG.nextLong(min, max);
    }

    public static long abs(long x) {
        return x < 0 ? -x : x;
    }

    public static long square(long x) {
        return times(x, x);
    }

    public static long pow(long x, long y) {
        long result = ONE;
        for (int i = 0; i < y / ONE; i++) {
            result = times(result, x);
        }
        return result;
    }

    public static long sqrt(long x) {
        if (x == 0 || x == ONE) { return x; }
        if (x < 0) { throw new SoftCalcException("SQRT of negative number."); }
        double full = Double.parseDouble(toStr(x));
        return (long) (Math.sqrt(full) * ONE);
    }
}
