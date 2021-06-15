package com.olexyn.abricore.fingers.sq;

import com.olexyn.abricore.fingers.DriverTools;
import com.olexyn.abricore.fingers.DriverTools.CRITERIA;
import com.olexyn.abricore.fingers.Login;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;




public class SqLogin extends Login {



    private static final String PREFIX = "swiss_iol_";
    private static final String USER = "user";
    private static final String PWD = "pwd";
    private static final String L3_NR = "current_l3_card_nr";
    protected static String CRED_PATH = "/docs/abricore/sq-credentials.json";




    @Override
    public WebDriver  doLogin() {

        Map<String,String> credentials = fetchCredentials();
        driver.get("https://www.swissquote.ch/url/login_bank?l=de");


        driver.findElement(By.name("username")).sendKeys(credentials.get("user"));
        SleepFactory.sleep(1);
        driver.findElement(By.name("password")).sendKeys(credentials.get("pwd"));
        SleepFactory.sleep(1);
        driver.findElement(By.id("loginText")).click();
        SleepFactory.sleep(1);

        WebElement keyHolder = DriverTools.getWhere(driver, "L3CodeDialog__challengeCode");
        String key = keyHolder.getText();
        SleepFactory.sleep(2);
        driver.findElement(By.className("L3CodeDialog__l3Code")).sendKeys(credentials.get(key));
        SleepFactory.sleep(2);
        driver.findElement(By.className("Button--primary")).click();
        SleepFactory.sleep(2);
        return driver;
     }

    @Override
    public boolean doLogout(WebDriver webDriver) {
        cleanup(webDriver);
        return true;
    }



    @Override
    protected Map<String,String> fetchCredentials() {
        Map<String,String> extractedMap = extractCredentialMap(CRED_PATH);
        Map<String,String> credentialMap = new HashMap<>();

        credentialMap.put(USER, extractedMap.get(PREFIX+USER));
        credentialMap.put(PWD, extractedMap.get(PREFIX + PWD));
        credentialMap.put(L3_NR, extractedMap.get(PREFIX + L3_NR));
        String value_prefix = PREFIX+ "l3_" + credentialMap.get(L3_NR) + "_";

        for (String key : extractedMap.keySet()) {
            if (key.startsWith(value_prefix)) {
                credentialMap.put( key.replace(value_prefix, ""), extractedMap.get(key));
            }
        }
        return credentialMap;
    }

}
