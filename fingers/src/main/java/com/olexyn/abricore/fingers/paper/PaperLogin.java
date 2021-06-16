package com.olexyn.abricore.fingers.paper;

import com.olexyn.abricore.fingers.Login;
import org.openqa.selenium.WebDriver;

import java.util.Map;

public class PaperLogin extends Login {

    @Override
    protected Map<String, String> fetchCredentials() {
        return null;
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
