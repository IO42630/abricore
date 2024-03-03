package com.olexyn.abricore.util;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for simple Property.
 */
public class ConfTest {

    @Test
    public void testProperties() throws IOException {

        System.setProperty("user.dir", System.getProperty("user.dir").replace("/util", ""));

        Property.loadProperties(Property.getEvents(), "events.properties");

        Property.getEvents().setProperty("test", "true");
        Property.saveProperties(Property.getEvents(), "events.properties");
        assertEquals("true", Property.get("test"));

        Property.getEvents().setProperty("test", "false");
        Property.saveProperties(Property.getEvents(), "events.properties");
        assertEquals("false", Property.get("test"));
    }
}
