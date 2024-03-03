package com.olexyn.abricore.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.olexyn.abricore.util.exception.CalcException;
import org.junit.Test;

import static com.olexyn.abricore.util.num.Num.EN2;
import static com.olexyn.abricore.util.num.Num.EN5;
import static com.olexyn.abricore.util.num.Num.EP1;
import static com.olexyn.abricore.util.num.Num.EP2;
import static com.olexyn.abricore.util.num.Num.EP3;
import static com.olexyn.abricore.util.num.Num.EP4;
import static com.olexyn.abricore.util.num.Num.EP5;
import static com.olexyn.abricore.util.num.Num.EP6;
import static com.olexyn.abricore.util.num.Num.EP7;
import static com.olexyn.abricore.util.num.Num.FIVE;
import static com.olexyn.abricore.util.num.Num.FOUR;
import static com.olexyn.abricore.util.num.Num.NINE;
import static com.olexyn.abricore.util.num.Num.N_ONE;
import static com.olexyn.abricore.util.num.Num.ONE;
import static com.olexyn.abricore.util.num.Num.P10;
import static com.olexyn.abricore.util.num.Num.P20;
import static com.olexyn.abricore.util.num.Num.P30;
import static com.olexyn.abricore.util.num.Num.P50;
import static com.olexyn.abricore.util.num.Num.P60;
import static com.olexyn.abricore.util.num.Num.SIX;
import static com.olexyn.abricore.util.num.Num.TEN;
import static com.olexyn.abricore.util.num.Num.THREE;
import static com.olexyn.abricore.util.num.Num.TWO;
import static com.olexyn.abricore.util.num.NumCalc.abs;
import static com.olexyn.abricore.util.num.NumCalc.div;
import static com.olexyn.abricore.util.num.NumCalc.num;
import static com.olexyn.abricore.util.num.NumCalc.pow;
import static com.olexyn.abricore.util.num.NumCalc.round;
import static com.olexyn.abricore.util.num.NumCalc.sqrt;
import static com.olexyn.abricore.util.num.NumCalc.times;
import static com.olexyn.abricore.util.num.NumSerialize.fromStr;
import static com.olexyn.abricore.util.num.NumSerialize.toStr;
import static com.olexyn.abricore.util.num.NumUtil.fromDouble;
import static com.olexyn.abricore.util.num.NumUtil.prettyStr;
import static com.olexyn.abricore.util.num.NumUtil.toInt;
import static org.junit.Assert.assertEquals;


public class CalcTest {

    @Test
    public void parseTest() throws CalcException {


//        Assert.assertEquals(of("10.10").num(), of("10"));
//        Assert.assertEquals("10.100000000", of("10.10").toString());
//        Assert.assertEquals(of("-10.10").num(), of("-10"));
//        Assert.assertEquals("-10.100000000", of("-10.10").toString());
//        Assert.assertEquals(of("10").dec(), of("0.0"));
//        Assert.assertEquals("10.000000000", of("10").toString());
//        Assert.assertEquals("10.000100000", of("10.0001").toString());
//        Assert.assertEquals("-10.000100000", of("-10.0001").toString());
//
//        Assert.assertEquals(of("010.10"), of("10.1"));
    }


    @Test
    public void plusTest() throws CalcException {
        assertEquals(P50, P20 + P30);
        assertEquals(fromStr("40"), fromStr("20") + fromStr("20"));
        assertEquals(fromStr("40.5"), fromStr("20.5") + fromStr("20"));
        assertEquals(fromStr("0.5"), fromStr("-20") + fromStr("20.5"));
    }

    @Test
    public void minusTest() {
        assertEquals(N_ONE, FIVE - SIX);
        assertEquals(ONE, SIX - FIVE);
        assertEquals(P10, P30 - P20);
        assertEquals(-P10, P20 - P30);
        assertEquals(N_ONE, -ONE);
    }

    @Test
    public void signTest() {
        assertEquals(EN2, abs(-EN2)); // -1.01 -> 1.01
        assertEquals(EN2, abs(EN2)); // 1.01 -> 1.01

    }



    @Test
    public void powerTest() {
        assertEquals(EP3, pow(EP1, THREE));
        assertEquals(EP4, pow(EP2, TWO));
    }

    @Test
    public void timesTest() throws CalcException {


        assertEquals(9000000000000000000L, times(EP5, EP4, NINE));
        assertEquals(9000000000000000000L, times(EP6, EP3, NINE));
        assertEquals(9000000000000000000L, times(EP7, EP2, NINE));
        assertEquals(9000000000000000000L, times(EP7 * 9, EP2));
        assertEquals(fromStr("50"), times(fromStr("10"), fromStr("5")));
        assertEquals(fromStr("50.5"), times(fromStr("10.1"), FIVE));
        assertEquals(fromStr("-50.5"), times(fromStr("-10.1"), FIVE));

        assertEquals(fromStr("5.05"), times(fromStr("1.01"), FIVE));
        assertEquals(fromStr("0.505"), times(fromStr("0.101"), FIVE));
        assertEquals(fromStr("0.0505"), times(fromStr("0.0101"), FIVE));
        assertEquals(fromStr("0.00505"), times(fromStr("0.00101"), FIVE));
        assertEquals(fromStr("0.000000505"), times(fromStr("0.000000101"), FIVE));
    }

    @Test
    public void roundTest() throws CalcException {
        assertEquals(ONE, num((ONE + EN2))); // 1.01 -> 1
        assertEquals(TEN, round(TEN + TWO, FIVE)); // 12 -> 10
        assertEquals(TEN + FIVE, round(TEN + FOUR, FIVE)); // 14 -> 15
    }

    @Test
    public void divTest() {
        assertEquals(fromStr("900000000"), div(fromStr("9000000000"), TEN));
        assertEquals(fromStr("90000000"), div(fromStr("900000000"), TEN));
        assertEquals(fromStr("-9000000"), div(fromStr("-90000000"), TEN));
        assertEquals(fromStr("900000"), div(fromStr("9000000"), TEN));
        assertEquals(fromStr("90000"), div(fromStr("900000"), TEN));
        assertEquals(fromStr("-9000"), div(fromStr("-90000"), TEN));
        assertEquals(fromStr("900"), div(fromStr("9000"), TEN));
        assertEquals(fromStr("90"), div(fromStr("900"), TEN));
        assertEquals(fromStr("900"), div(fromStr("90"), P10));
        assertEquals(fromStr("90"), div(fromStr("9"), P10));
        assertEquals(fromStr("9"), div(fromStr("0.9"), P10));
        assertEquals(fromStr("0.9"), div(fromStr("0.09"), P10));
        assertEquals(fromStr("0.09"), div(fromStr("0.009"), P10));
        assertEquals(fromStr("-0.009"), div(fromStr("-0.0009"), P10));
        assertEquals(fromStr("0.0009"), div(fromStr("0.00009"), P10));
        assertEquals(fromStr("0.00009"), div(fromStr("0.000009"), P10));
        assertEquals(fromStr("0.000009"), div(fromStr("0.0000009"), P10));
        assertEquals(fromStr("-0.0000009"), div(fromStr("-0.00000009"), P10));
        assertEquals(fromStr("0.00000009"), div(fromStr("0.000000009"), P10));

        assertEquals(TWO, div(TEN, FIVE));
        assertEquals(P50, div(FIVE, TEN));
        assertEquals(div(fromStr("56.65"), fromStr("-5.5")), fromStr("-10.3"));
        assertEquals(div(fromStr("0.000000015"), fromStr("0.0003")), fromStr("0.00005"));
    }

    @Test
    public void miscTest() {

        assertEquals(TWO, sqrt(FOUR)); // 4 -> 2
        assertEquals(FOUR, sqrt(TEN + SIX)); // 4 -> 2
        assertEquals(P50, sqrt(fromStr("0.25"))); // 0.25 -> 0.5
        assertEquals(fromStr("0.05"), sqrt(fromStr("0.0025")));
        assertEquals(fromStr("0.0005"), sqrt(fromStr("0.00000025")));
        assertEquals(fromStr("0.0002"), sqrt(fromStr("0.00000004")));

    }

    @Test
    public void toStringTest() {

        assertEquals(ONE + P10, fromStr(toStr(ONE + P10)));
        assertEquals(ONE, fromStr(toStr(ONE)));
        assertEquals(N_ONE, fromStr(toStr(N_ONE)));
        assertEquals(P10, fromStr(toStr(P10)));
        assertEquals(-P10, fromStr(toStr(-P10)));
        assertEquals(0, fromStr(toStr(0)));
        assertEquals(0, fromStr("-0"));
        assertEquals(0, fromStr("0.0"));
        assertEquals(0, fromStr("-0.0xx"));

        assertEquals(ONE + P10, fromStr("1.1"));

        assertEquals(EN5, fromStr("0.00001000000000000001"));

        assertEquals(P60, fromDouble(0.6));
        assertEquals("0.01", prettyStr(EN2, 2));
        assertEquals("1", prettyStr(fromStr("1.01"), 0));

    }


    @Test
    public void jsonTest() throws JsonProcessingException {
    }


    @Test
    public void toIntTest() throws JsonProcessingException {
        assertEquals(Integer.MAX_VALUE, toInt(((long) Integer.MAX_VALUE) * ONE));
        assertEquals(Integer.MIN_VALUE, toInt(((long) Integer.MIN_VALUE) * ONE));
    }



}


