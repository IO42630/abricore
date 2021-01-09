package com.olexyn.abricore.fingers;

import org.openqa.selenium.WebDriver;

public abstract class Fetch {

    protected final WebDriver webDriver;

    protected Fetch(WebDriver webDriver) {
        this.webDriver = webDriver;
    }
}
