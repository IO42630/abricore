package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.fingers.Login;
import org.openqa.selenium.WebDriver;

public class TwLogin extends Login {

    @Override
    protected WebDriver init() {
        return null;
    }

    @Override
    protected boolean cleanup(WebDriver webDriver) {
        return false;
    }

    @Override
    public WebDriver doLogin() {
        return null;
    }

    @Override
    public boolean doLogout(WebDriver webDriver) {
        return false;
    }
}
