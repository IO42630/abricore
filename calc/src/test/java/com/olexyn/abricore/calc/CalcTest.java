package com.olexyn.abricore.calc;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CalcTest {

    @Test
    public void parseLongTest() {
        Assert.assertEquals(15000L, Calc.parseLong("15"));
        Assert.assertEquals(15000L, Calc.parseLong("15.0"));
        Assert.assertEquals(15810L, Calc.parseLong("15.81"));
        Assert.assertEquals(15030L, Calc.parseLong("15.0303"));
        Assert.assertEquals(15001L, Calc.parseLong("15.0005"));
        Assert.assertEquals(11235290L, Calc.parseLong("11235.2898"));
        Assert.assertEquals(15290L, Calc.parseLong("15.2898"));
        Assert.assertEquals(17000L, Calc.parseLong("16.9995"));
    }
}
