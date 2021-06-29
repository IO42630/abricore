package com.olexyn.abricore.fingers;

import com.olexyn.abricore.fingers.sq.SleepFactory;
import com.olexyn.abricore.util.Constants;
import com.olexyn.abricore.util.FileUtil;
import com.olexyn.abricore.util.Parameters;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public abstract class Session {

    private static final String CHROME_DRIVER = "chromedriver_92";

    protected boolean active = false;

    private static WebDriver driver = null;



    public static WebDriver getDriver() {
        if (driver == null) {
            init();
        }
        return driver;
    }

    public static List<String> getTabs() {
        return new ArrayList<>(getDriver().getWindowHandles());
    }

    protected static void init() {

        String pathStr = System.getProperty("user.dir") + "/fingers/src/main/resources/" + CHROME_DRIVER;
        ChromeDriverService service = new ChromeDriverService.Builder()
            .usingDriverExecutable(new File(pathStr))
            .usingAnyFreePort()
            .build();

        String downloadFilepath = Parameters.QUOTES_DIR_TMP;
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", downloadFilepath);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        DesiredCapabilities cap = DesiredCapabilities.chrome();
        cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        cap.setCapability(ChromeOptions.CAPABILITY, options);

        driver = new ChromeDriver(service, cap);

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
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

    public static WebElement filterElementListBy(List<WebElement> list, CRITERIA criteria, String text) {
        for (WebElement element : list) {
            String toEvaluate = null;
            switch (criteria) {
                case CLASS:
                    toEvaluate = element.getClass().getName();
                    break;
                case TEXT:
                    toEvaluate = element.getText();
                    break;
                case TAG:
                    toEvaluate = element.getTagName();
                    break;
                case HREF:
                    toEvaluate = element.getAttribute("href");
                    break;
                case ID:
                    toEvaluate = element.getAttribute("id");
                    break;
                case TITLE:
                    toEvaluate = element.getAttribute("title");
                    break;
                case NONE:
                    toEvaluate = text;
            }
            if (toEvaluate != null && toEvaluate.contains(text)) { return element; }
        }
        return  null;
    }

    private static final String FRAME_ID_DEFAULT_CONTENT = "defaultContent";
    private static final String FRAME_ID_NONE_FOUND = "noneFound";

    /**
     * Collects all frames accessible to WebDriver.
     */
    public static Map<String,String> collectAllFrames(WebDriver driver) throws NoSuchFrameException {
        Map<String, String> mapOfCollectedSources = new HashMap<>();
        driver.switchTo().defaultContent();
        mapOfCollectedSources.put(FRAME_ID_DEFAULT_CONTENT, driver.getPageSource());
        for (int i = 0; i< 10 ; i++) {
            try {
                driver.switchTo().defaultContent();
                driver.switchTo().frame(i);
                mapOfCollectedSources.put(String.valueOf(i), driver.getPageSource());
            } catch (NoSuchFrameException e) {
                return  mapOfCollectedSources;
            }
        }
        return  mapOfCollectedSources;
    }

    public static String findFrameContainingCharSeq(Map<String,String> mapOfCollectedSources , String string) {
        for (Entry<String, String> entry : mapOfCollectedSources.entrySet()) {
            if (entry.getValue().contains(string)) {
                return entry.getKey();
            }
        }
        return FRAME_ID_NONE_FOUND;
    }

    public enum CRITERIA{
        CLASS,
        TEXT,
        TAG,
        HREF,
        NONE,
        ID,
        TITLE
    }



    public static void switchToFrameContainingCharSeq(WebDriver driver, String charSeq) {
        driver.switchTo().defaultContent();
        SleepFactory.sleep(2);
        final String frameId= findFrameContainingCharSeq(collectAllFrames(driver), charSeq);
        SleepFactory.sleep(2);
        switch (frameId) {
            case FRAME_ID_DEFAULT_CONTENT:
                driver.switchTo().defaultContent();
                break;
            case FRAME_ID_NONE_FOUND:
                break;
            default:
                driver.switchTo().frame(Integer.parseInt(frameId));
        }
    }

    /**
     * Return the first occurrence of specified class that has specified label.
     */
    public static WebElement getWhere(WebDriver driver, String className, CRITERIA criteria, String text) {
        switchToFrameContainingCharSeq(driver, text);
        List<WebElement> elements = driver.findElements(By.className(className));
        return filterElementListBy(elements, criteria, text);
    }

    public static WebElement getWhere(WebDriver driver, String className) {
        switchToFrameContainingCharSeq(driver, className);
        List<WebElement> elements = driver.findElements(By.className(className));
        return filterElementListBy(elements, CRITERIA.NONE, "");
    }

    public static void followContainedLink(WebDriver driver, WebElement element) {
        String link = element.getAttribute("href");
        if (link != null) driver.navigate().to(link);
    }



    public static void setRadio(WebDriver driver, By by, boolean checked) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].checked = "+ checked + ";", driver.findElement(by));
    }

    public static void setComboByDataValue(WebDriver driver, By comboBy, String dataValue) {
        WebElement combo = driver.findElement(comboBy);
        combo.click();
        combo.findElement(By.cssSelector("li[data-value='" + dataValue + "']")).click();
    }

    public static WebElement getByFieldValue(WebDriver driver, String type, String field, String value) {
        return driver.findElement(By.cssSelector(type + "["+ field + "='" + value + "']"));
    }

    public static WebElement getByFieldValue(WebElement element, String type, String field, String value) {
        return element.findElement(By.cssSelector(type + "["+ field + "='" + value + "']"));
    }

    public static WebElement getByText(WebDriver driver, String text) {
        return driver.findElement (By.xpath ("//*[contains(text(),'" + text + "')]"));
    }

    public static void sendDeleteKeys(WebElement element, int n) {
        for (int i = 0; i < n; i++) {
            element.sendKeys(Keys.BACK_SPACE);
        }
    }



    public static void goToTab(String url) {
        for (String tab : getTabs()) {
            if (tab.equals(url)) {
                getDriver().switchTo().window(tab);
            }
        }
    }

    public static void newTab() {
        newTab(Constants.EMPTY);
    }

    public static void newTab(String url) {
        String currentUrl = Session.getDriver().getCurrentUrl();

        if (currentUrl.isEmpty()
            || currentUrl.equals("data:,")
            || currentUrl.equals("about:blank")) {
            return;
        }
        execute("window.open(arguments[0]),url");
    }

    public static void execute(String script) {
        ((JavascriptExecutor) Session.getDriver()).executeScript(script);
    }

}
