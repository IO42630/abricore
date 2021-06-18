package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.fingers.DriverTools;
import com.olexyn.abricore.fingers.DriverTools.CRITERIA;
import com.olexyn.abricore.fingers.Session;
import com.olexyn.abricore.fingers.sq.SleepFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;
import java.util.Map;

public class TwSession extends Session {

    private static final String PREFIX = "tw_iol_";
    private static final String USER = "user";
    private static final String PWD = "pwd";
    private static final String CRED_PATH = "/docs/abricore/tw-credentials.json";

    @Override
    public WebDriver doLogin() {
        if (active) {
            return driver;
        }
        Map<String,String> credentials = fetchCredentials();
        driver.get("https://www.tradingview.com/#signin");

        driver.findElement(By.className("tv-signin-dialog__toggle-email")).click();

        driver.findElement(By.name("username")).sendKeys(credentials.get("user"));
        SleepFactory.sleep(1);
        driver.findElement(By.name("password")).sendKeys(credentials.get("pwd"));
        SleepFactory.sleep(1);
        DriverTools.getWhere(driver, "tv-button", CRITERIA.ID, "email-signin__submit-button").click();

        active = true;
        return driver;
    }



    @Override
    protected Map<String, String> fetchCredentials() {
        Map<String,String> extractedMap = extractCredentialMap(CRED_PATH);
        Map<String,String> credentialMap = new HashMap<>();

        credentialMap.put(USER, extractedMap.get(PREFIX+USER));
        credentialMap.put(PWD, extractedMap.get(PREFIX + PWD));
        return credentialMap;
    }

}
