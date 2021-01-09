package com.olexyn.abricore.fingers;

import org.openqa.selenium.WebDriver;

public abstract class Login {

    protected abstract WebDriver init();
    protected abstract boolean cleanup(WebDriver webDriver);
    public abstract WebDriver doLogin();

    public abstract  boolean doLogout(WebDriver webDriver);


}
