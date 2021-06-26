package com.olexyn.abricore.flow;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple Main.
 */
public class PropertiesTest extends Main {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void testProperties() throws IOException {
        Main.loadProperties(events, "events_test.properties");

        Main.events.setProperty("tw.last.download", "2019-01-03T07:00:00Z");
        Main.saveProperties(events, "events_test.properties");
        assertEquals(Main.events.getProperty("tw.last.download"), "2019-01-03T07:00:00Z");

        Main.events.setProperty("tw.last.download", "2018-01-03T07:00:00Z");
        Main.saveProperties(events, "events_test.properties");
        assertEquals(Main.events.getProperty("tw.last.download"), "2018-01-03T07:00:00Z");
    }
}
