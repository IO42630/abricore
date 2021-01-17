package com.olexyn.abricore.fingers;

import org.openqa.selenium.WebDriver;

public abstract class Fetch {

    protected final WebDriver driver;

    protected Fetch(WebDriver driver) {
        this.driver = driver;
    }
}
