package com.olexyn.abricore.calc;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
}
