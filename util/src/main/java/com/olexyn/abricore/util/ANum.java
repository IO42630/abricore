package com.olexyn.abricore.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.olexyn.abricore.util.Constants.DOT;
import static com.olexyn.abricore.util.Constants.DOT_REGEX;
import static com.olexyn.abricore.util.Constants.NULL;
import static com.olexyn.abricore.util.Constants.ZERO_STR;

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

    public static final int DEC_IN_NUM = 1000000000;
    public static final ANum ZERO = new ANum(0, 0);

    public ANum(long num, int dec) {
        this.num = num;
        this.dec = dec;
    }

    public ANum(long num) {
        this(num, 0);
    }

    private ANum() {}


    public static ANum of(String string) {
        if (string == null || string.equals(NULL)) {
            return null;
        }

        if (string.contains(DOT)) {
            String[] split = string.split(DOT_REGEX);
            long num = Long.parseLong(split[0]);
            StringBuilder decStringBuilder = new StringBuilder(split[1]);
            while (decStringBuilder.length() < 9) {
                decStringBuilder.append(ZERO_STR);
            }
            int dec = Integer.parseInt(decStringBuilder.substring(0, 9));
            return new ANum(num, dec);
        } else {
            System.out.println(string);
            long num = Long.parseLong(string);
            int dec = 0;
            return new ANum(num, dec);
        }
    }

    public static ANum of(BigDecimal bigDecimal) {
        return of(bigDecimal.toString());
    }

    @Override
    public String toString() {
        StringBuilder zeroSb = new StringBuilder();
        String decString = String.valueOf(dec);
        while (zeroSb.length() + decString.length() < 9) {
            zeroSb.append(ZERO_STR);
        }
        return String.join(DOT, String.valueOf(num), zeroSb.append(decString).toString());
    }

    public String toString(int i) {
        if (!toString().contains(".")) {
            return toString();
        }
        return toString().split("\\.")[0] + "." + toString().split("\\.")[1].substring(0, i);
    }

    private BigDecimal toBigDecimal() {
        return new BigDecimal(toString());
    }

    public ANum plus(ANum other) {

        if (this.geq(ZERO) && other.lesser(ZERO)) {
            return this.minus(other.neg());
        }

        if (this.geq(ZERO) && other.geq(ZERO)) {
            ANum result = new ANum();
            result.num = num + other.num;
            result.dec = dec + other.dec;

            if (result.dec >= DEC_IN_NUM) {
                result.dec -= DEC_IN_NUM;
                result.num += 1;
            }
            return result;
        }

        if (this.lesser(ZERO) && other.lesser(ZERO)) {
            ANum negThis = this.neg();
            ANum negOther = other.neg();
            return negThis.plus(negOther).neg();
        }

        if (this.lesser(ZERO) && other.geq(ZERO)) {
            return doMinus(other, this.neg());
        }

        return null;
    }

    public ANum minus(ANum other) {
        if (this.greater(other)) {
            return doMinus(this, other);
        } else {
            return doMinus(other, this).neg();
        }
    }

    /**
     * a > b
     */
    private static ANum doMinus(ANum a, ANum b) {

        ANum result = new ANum();

        if (a.num >= 0 && b.num >= 0) {
            result.num = a.num - b.num;
            result.dec = a.dec - b.dec;
            if (result.dec < 0) {
                result.dec += DEC_IN_NUM;
                result.num -= 1;
            }
        }
        return result;
    }

    public ANum times(ANum other) {
        long preProdDec = (long) dec * other.dec / DEC_IN_NUM;
        long prodDec = (long) dec * other.abs().num + (long) other.dec * abs().num + preProdDec;
        long prodNum = num * other.num;
        if (prodDec > DEC_IN_NUM){
            long over = prodDec / DEC_IN_NUM;
            prodNum += over;
            prodDec -= over * DEC_IN_NUM;
        }
        return new ANum(prodNum, (int) prodDec);
    }

    public ANum div(ANum other) {
        return of(this.toBigDecimal().divide(other.toBigDecimal(), RoundingMode.HALF_EVEN));
    }

    public boolean lesser(ANum other) {
        boolean b1 = num < other.num;
        boolean b2 = num == other.num && dec < other.dec;
        return b1 || b2;
    }

    public boolean greater(ANum other) {
        boolean b1 = num > other.num;
        boolean b2 = num == other.num && dec > other.dec;
        return b1 || b2;
    }

    public boolean leq(ANum other) {
        return !greater(other);
    }

    public boolean geq(ANum other) {
        return !lesser(other);
    }

    public ANum neg() {
        return new ANum(-num, dec);
    }

    public ANum abs() {
        return lesser(ZERO) ? neg() : copy() ;
    }

    public ANum copy() {
        return new ANum(num, dec);
    }

    public ANum num() {
        return new ANum(num, 0);
    }

    public ANum dec() {
        return new ANum(0, dec);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ANum aNum = (ANum) o;
        return num == aNum.num && dec == aNum.dec;
    }

    public void mergeFrom(ANum newValue) {
        if (newValue != null) {
            this.num = newValue.num;
            this.dec = newValue.dec;
        }
    }

}
