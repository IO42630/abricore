package com.olexyn.abricore.util;

import com.olexyn.abricore.util.exception.CalcException;

import static com.olexyn.abricore.util.Constants.DOT;
import static com.olexyn.abricore.util.Constants.DOT_REGEX;
import static com.olexyn.abricore.util.Constants.ZERO;

/**
 * MAX ANum :
 * 9 223 372 036 854 775 807
 * MAX Integer :
 * 2 147 483 647
 * MAX ANum :
 * 9 223 372 036 854 775 807 . 999 999 999
 */
public class ANum {

    private long num;
    private int dec;

    private static final int DEC_LIMIT = 999999999;

    public ANum(long num, int dec) {
        this.num = num;
        this.dec = dec;
    }

    public ANum(long num) {
        this(num, 0);
    }



    public static ANum of(String string) {
        String[] split = string.split(DOT_REGEX);
        long num = Long.parseLong(split[0]);
        StringBuilder decStringBuilder = new StringBuilder(split[1]);
        while (decStringBuilder.length() != 8) {
            decStringBuilder.append(ZERO);
        }
        int dec = Integer.parseInt(decStringBuilder.toString());
        return  new ANum(num, dec);
    }

    @Override
    public String toString() {
        return String.join(DOT, String.valueOf(num), String.valueOf(dec));
    }

    public ANum add(ANum other) {
        if (other.dec < 0 || other.num < 0) {
            throw new CalcException();
        }
        dec += other.dec;
        if (dec > DEC_LIMIT) {
            dec -= DEC_LIMIT + 1;
            num++;
        }
        num += other.num;
        return this;
    }

    public ANum sub(ANum other) {

        //
        // 1.2 - 2.4 = - 1.2
        // 1.4 - 2.2 = - 0.8
        //
        num -= other.num;
        int tempDec = dec - other.dec;
        if (num < 0) {
            if (tempDec < 0 ) {
                dec = other.dec - dec;
            } else {
                dec = DEC_LIMIT + 1 + other.dec - dec;
                num++;
            }
        } else {
            if (tempDec < 0) {
                dec += DEC_LIMIT + 1;
                num--;
            }
        }


        dec -= other.dec;

        return this;


    }

    public ANum mul(ANum other) {
        return this;
    }

    public ANum div(ANum other) {
        return this;
    }

    public boolean lesser(ANum other) {
        return true;
    }

    public boolean greater(ANum other) {
        return true;
    }

    public boolean leq(ANum other) {
        return true;
    }

    public boolean geq(ANum other) {
        return true;
    }

    public ANum neg() {
        return null;
    }
}
