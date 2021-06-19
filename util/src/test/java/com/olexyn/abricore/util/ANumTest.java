package com.olexyn.abricore.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ANumTest {

    @Test
    public void parseTest() {


        Assert.assertEquals(ANum.of("10.10"), new ANum(10, 100000000));
        Assert.assertEquals(new ANum(10, 100000000).toString(), "10.100000000");
        Assert.assertEquals(ANum.of("-10.10"), new ANum(-10, 100000000));
        Assert.assertEquals(new ANum(-10, 100000000).toString(), "-10.100000000");
        Assert.assertEquals(ANum.of("10"), new ANum(10));
        Assert.assertEquals(new ANum(10).toString(), "10.000000000");
        Assert.assertEquals(ANum.of("10.0001"), new ANum(10, 100000));
        Assert.assertEquals(new ANum(10, 100000).toString(), "10.000100000");
        Assert.assertEquals(ANum.of("-10.0001"), new ANum(-10, 100000));
        Assert.assertEquals(new ANum(-10, 100000).toString(), "-10.000100000");

        Assert.assertEquals(ANum.of("010.10"), new ANum(10, 100000000));
    }


    @Test
    public void plusTest() {
        Assert.assertEquals(ANum.of("10").plus(ANum.of("20")), ANum.of("30"));
        Assert.assertEquals(ANum.of("10.001").plus(ANum.of("20.002")), ANum.of("30.003"));
        Assert.assertEquals(ANum.of("-10.001").plus(ANum.of("20.002")), ANum.of("10.001"));
        Assert.assertEquals(ANum.of("10.001").plus(ANum.of("-20.002")), ANum.of("-10.001"));
        Assert.assertEquals(ANum.of("-10.001").plus(ANum.of("-20.002")), ANum.of("-30.003"));
        Assert.assertEquals(ANum.of("-0.001").plus(ANum.of("-0.002")), ANum.of("-0.003"));
        Assert.assertEquals(ANum.of("-0.000").plus(ANum.of("00.000")), ANum.of("-00.00"));
        Assert.assertEquals(ANum.of("10.6").plus(ANum.of("20.6")), ANum.of("31.2"));
        Assert.assertEquals(ANum.of("10.2").plus(ANum.of("-20.6")), ANum.of("-10.4"));
        Assert.assertEquals(ANum.of("10.6").plus(ANum.of("-20.2")), ANum.of("-9.6"));
    }

    @Test
    public void minusTest() {
        Assert.assertEquals(ANum.of("10").minus(ANum.of("5")), ANum.of("5"));
    }

    @Test
    public void compareTest() {
        Assert.assertTrue(ANum.of("10").leq(ANum.of("10")));
        Assert.assertTrue(ANum.of("10").lesser(ANum.of("10.1")));
    }

    @Test
    public void timesTest() {
        Assert.assertEquals(ANum.of("10").times(ANum.of("5")), ANum.of("50"));
        Assert.assertEquals(ANum.of("10.1").times(ANum.of("5")), ANum.of("50.5"));
        Assert.assertEquals(ANum.of("-10.1").times(ANum.of("5")), ANum.of("-50.5"));
        Assert.assertEquals(ANum.of("10.1").times(ANum.of("-5")), ANum.of("-50.5"));
        Assert.assertEquals(ANum.of("-10.1").times(ANum.of("-5")), ANum.of("50.5"));
        Assert.assertEquals(ANum.of("-10.3").times(ANum.of("-5.5")), ANum.of("56.65"));
        Assert.assertEquals(ANum.of("5.00000000").times(ANum.of("0.000000003")), ANum.of("0.000000015"));
        Assert.assertEquals(ANum.of("0.00005").times(ANum.of("0.0003")), ANum.of("0.000000015"));
    }

    @Test
    public void divTest() {
        Assert.assertEquals(ANum.of("10").div(ANum.of("5")), ANum.of("2"));
        Assert.assertEquals(ANum.of("56.65").div(ANum.of("-5.5")), ANum.of("-10.3"));
        Assert.assertEquals(ANum.of("0.000000015").div(ANum.of("0.0003")), ANum.of("0.00005"));
    }

    @Test
    public void miscTest() {
        ANum n = new ANum(1,0);
        Assert.assertEquals(n, n.copy());
        Assert.assertEquals(ANum.of("-1.32").abs(), ANum.of("1.32"));
    }

}


