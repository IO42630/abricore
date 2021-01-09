package com.olexyn.abricore.fingers.tw;

import com.olexyn.abricore.fingers.Login;
import com.olexyn.abricore.fingers.sq.SleepFactory;
import com.olexyn.abricore.fingers.sq.Tools;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TwLogin extends Login {

    private static final String PREFIX = "tw_iol_";
    private static final String USER = "user";
    private static final String PWD = "pwd";
    private static final String CRED_PATH = "/docs/abricore/tw-credentials.json";

    @Override
    public WebDriver doLogin() {
        Map<String,String> credentials = fetchCredentials();
        driver.get("https://www.tradingview.com/#signin");

        driver.findElement(By.className("tv-signin-dialog__toggle-email")).click();


        getWhere("mn-Dropdown__trigger", "LOGIN").click();
        followContainedLink(driver, getWhere("mn-Dropdown__text", "Login Bank"));

        driver.findElement(By.name("username")).sendKeys(credentials.get("user"));
        SleepFactory.sleep(1);
        driver.findElement(By.name("password")).sendKeys(credentials.get("pwd"));
        SleepFactory.sleep(1);
        driver.findElement(By.id("loginText")).click();
        SleepFactory.sleep(2);

        WebElement keyHolder = getWhere("L3CodeDialog__challengeCode");
        String key = keyHolder.getText();
        driver.findElement(By.className("js-l3Code L3CodeDialog__l3Code Input Field Field__input")).sendKeys(credentials.get(key));
        driver.findElement(By.className("js-authenticate Button Button--primary")).click();

        return driver;
    }

    @Override
    public boolean doLogout(WebDriver webDriver) {
        return false;
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
