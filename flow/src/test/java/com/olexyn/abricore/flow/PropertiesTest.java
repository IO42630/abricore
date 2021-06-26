package com.olexyn.abricore.flow;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple Main.
 */
public class PropertiesTest{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testProperties() throws IOException {

        System.setProperty("user.dir", System.getProperty("user.dir").replace("/flow", ""));

        Main.loadProperties(Main.events, "events.properties");

        Main.events.setProperty("test", "true");
        Main.saveProperties(Main.events, "events.properties");
        assertEquals(Main.events.getProperty("test"), "true");

        Main.events.setProperty("test", "false");
        Main.saveProperties(Main.events, "events.properties");
        assertEquals(Main.events.getProperty("test"), "false");
    }
}
