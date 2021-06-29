package com.olexyn.abricore.flow;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple MainApp.
 */
public class PropertiesTest{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testProperties() throws IOException {

        System.setProperty("user.dir", System.getProperty("user.dir").replace("/flow", ""));

        MainApp.loadProperties(MainApp.events, "events.properties");

        MainApp.events.setProperty("test", "true");
        MainApp.saveProperties(MainApp.events, "events.properties");
        assertEquals(MainApp.events.getProperty("test"), "true");

        MainApp.events.setProperty("test", "false");
        MainApp.saveProperties(MainApp.events, "events.properties");
        assertEquals(MainApp.events.getProperty("test"), "false");
    }
}
