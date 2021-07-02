package com.olexyn.abricore.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple Param.
 */
public class ParamTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testProperties() throws IOException {

        System.setProperty("user.dir", System.getProperty("user.dir").replace("/util", ""));

        Param.loadProperties(Param.events, "events.properties");

        Param.events.setProperty("test", "true");
        Param.saveProperties(Param.events, "events.properties");
        assertEquals(Param.events.getProperty("test"), "true");

        Param.events.setProperty("test", "false");
        Param.saveProperties(Param.events, "events.properties");
        assertEquals(Param.events.getProperty("test"), "false");
    }
}
