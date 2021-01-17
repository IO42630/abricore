package com.olexyn.abricore.fingers;

import com.olexyn.abricore.fingers.sq.SleepFactory;
import com.olexyn.abricore.fingers.sq.Tools;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public abstract class Login {

    protected WebDriver driver;

    protected Login() {
        this.driver = init();
    }

    protected WebDriver init() {
        try {
            String path = App.class.getClassLoader().getResource("chromedriver_83").getPath();
            System.setProperty("webdriver.chrome.driver", path);
        } catch (NullPointerException ignored){}

        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    protected boolean cleanup(WebDriver driver){
        if(driver !=null)
            driver.quit();
        return true;
    }

    /**
     * Get credentails from JSON at Path
     */
    protected Map<String,String> extractCredentialMap(String cred_path) {
        Map<String,String> extractedMap = new HashMap<>();

        String path = System.getProperty("user.home") + cred_path;
        String contents = new Tools().fileToString(new File(path));
        JSONObject obj ;
        try {
            obj = new JSONObject(contents);
            for (String key : obj.keySet()) {
                extractedMap.put(key, obj.getString(key));
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        return extractedMap;
    }

    protected  abstract Map<String,String> fetchCredentials();

    public abstract WebDriver doLogin();
    public abstract  boolean doLogout(WebDriver webDriver);

}
