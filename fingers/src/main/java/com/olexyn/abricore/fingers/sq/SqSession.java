package com.olexyn.abricore.fingers.sq;

import com.olexyn.abricore.fingers.Session;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;




public class SqSession extends Session {



    private static final String PREFIX = "swiss_iol_";
    private static final String USER = "user";
    private static final String PWD = "pwd";
    private static final String L3_NR = "current_l3_card_nr";
    protected static String CRED_PATH = "/docs/abricore/sq-credentials.json";




    @Override
    public void doLogin() {
        if (active) {
            return;
        }
        Map<String,String> credentials = fetchCredentials();
        synchronized (Session.class){
            newTab();
            DRIVER.get("https://www.swissquote.ch/url/login_bank?l=de");

            DRIVER.findElement(By.name("username")).sendKeys(credentials.get("user"));
            SleepFactory.sleep(1);
            DRIVER.findElement(By.name("password")).sendKeys(credentials.get("pwd"));
            SleepFactory.sleep(1);
            DRIVER.findElement(By.id("loginText")).click();
            SleepFactory.sleep(1);

            WebElement keyHolder = Session.getWhere("L3CodeDialog__challengeCode");
            String key = keyHolder.getText();
            SleepFactory.sleep(2);
            DRIVER.findElement(By.className("L3CodeDialog__l3Code")).sendKeys(credentials.get(key));
            SleepFactory.sleep(2);
            DRIVER.findElement(By.className("Button--primary")).click();
            SleepFactory.sleep(2);
        }
        active = true;
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
