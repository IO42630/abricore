package com.olexyn.abricore.util;

import org.junit.Assert;
import org.junit.Test;

public class CalcTest {

    @Test
    public void parseLongTest() {
        Assert.assertNull(Calc.parseLong("null"));
        Assert.assertEquals(Long.valueOf(15000L), Calc.parseLong("15"));
        Assert.assertEquals(Long.valueOf(15000L), Calc.parseLong("15.0"));
        Assert.assertEquals(Long.valueOf(15810L), Calc.parseLong("15.81"));
        Assert.assertEquals(Long.valueOf(15030L), Calc.parseLong("15.0303"));
        Assert.assertEquals(Long.valueOf(15001L), Calc.parseLong("15.0005"));
        Assert.assertEquals(Long.valueOf(11235290L), Calc.parseLong("11235.2898"));
        Assert.assertEquals(Long.valueOf(15290L), Calc.parseLong("15.2898"));
        Assert.assertEquals(Long.valueOf(17000L), Calc.parseLong("16.9995"));
    }

    @Test
    public void multiplyTest() {
        Assert.assertEquals(Long.valueOf(8000L), Calc.multiply(2000L, 4000L));
        Assert.assertEquals(Long.valueOf(8020L), Calc.multiply(2005L, 4000L));
    }
}
