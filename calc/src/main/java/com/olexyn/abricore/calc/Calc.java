package com.olexyn.abricore.calc;

import java.math.BigDecimal;

public class Calc {

    public final static int PRECISION = 3;
    public final static int MAX_VALUE = 3000001;



    public static void main(String... args){
        //Long number = parseLong("3000001");
        //Long result = number * number;
        //int br = 0;

        String fgoo = parseString(123124L);
        int br =  0;
    }


    public static String parseString(Long l) {
        String full = l.toString();
        String whole = full.substring(0, full.length() - PRECISION);
        String floating = full.substring(full.length() - PRECISION);
        return whole + "." + floating;
    }


    public static long parseLong(String s) {
        double d = Double.parseDouble(s);
        d = d * Math.pow(10, PRECISION);
        return Math.round(d);
    }
}
