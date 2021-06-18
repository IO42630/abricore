package com.olexyn.abricore.fingers;

import com.olexyn.abricore.util.FileUtil;
import com.olexyn.abricore.util.Parameters;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class Session {

    private static final String CHROME_DRIVER = "chromedriver_92";

    protected boolean active = false;

    protected WebDriver driver;

    protected Session() {
        this.driver = init();
    }

    protected WebDriver init() {
        try {
            String path = Session.class.getClassLoader().getResource(CHROME_DRIVER).getPath();
            System.setProperty("webdriver.chrome.driver", path);
        } catch (NullPointerException e){
            e.printStackTrace();
        }




        String downloadFilepath = Parameters.QUOTES_DIR_TMP;
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", downloadFilepath);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);
        driver = new ChromeDriver(cap);


        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    public void doLogout() {
        if (active) {
            cleanup(driver);
            active = false;
        }
    }

    protected void cleanup(WebDriver driver){
        if(driver !=null)
            driver.quit();
    }

    /**
     * Get credentails from JSON at Path
     */
    protected Map<String,String> extractCredentialMap(String cred_path) {
        Map<String,String> extractedMap = new HashMap<>();

        String path = System.getProperty("user.home") + cred_path;
        String contents = new FileUtil().fileToString(new File(path));
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

}
