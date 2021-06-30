package com.olexyn.abricore.fingers;

import com.olexyn.abricore.fingers.sq.SleepFactory;
import com.olexyn.abricore.util.Constants;
import com.olexyn.abricore.util.FileUtil;
import com.olexyn.abricore.util.LogUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.olexyn.abricore.fingers.TabPurpose.SQ_SESSION;
import static com.olexyn.abricore.fingers.TabPurpose.TW_SESSION;

public abstract class Session {

    private static final Logger LOGGER = LogUtil.get(Session.class);

    private static final String CHROME_DRIVER = "chromedriver_92";

    protected boolean active = false;

    public final static Map<String, Tab> TABS = new HashMap<>();

    public final static WebDriver DRIVER = init();



    public static void registerCurrentTab(TabPurpose purpose) {
        Tab tab = new Tab(DRIVER.getWindowHandle());
        tab.setName(DRIVER.getTitle());
        tab.setUrl(DRIVER.getCurrentUrl());
        tab.setPurpose(purpose);
        TABS.put(tab.getHandle(), tab);
    }

    public static Tab getCurrentTab() {
        return TABS.get(DRIVER.getWindowHandle());
    }

    public static List<Tab> getTabByPurpose(TabPurpose purpose) {
        return TABS.values().stream()
            .filter( x -> x.getPurpose() == purpose)
            .collect(Collectors.toList());
    }

    public static String registerBlankTab(TabPurpose purpose) {
        Set<String> openTabHandles = DRIVER.getWindowHandles();
        for (String openTabHandle : openTabHandles) {
                if (!TABS.containsKey(openTabHandle)) {
                    Tab blankTab = new Tab(openTabHandle);
                    blankTab.setName("about:blank");
                    blankTab.setUrl("about:blank");
                    blankTab.setPurpose(purpose);
                    TABS.put(openTabHandle, blankTab);
                    return openTabHandle;
                }
        }
        LOGGER.warning("Not unregistered tab found.");
        return null;
    }

    public static String registerExistingTab(TabPurpose purpose) {
        String handle = DRIVER.getWindowHandle();
        Tab tab = new Tab(handle);
        tab.setName(DRIVER.getTitle());
        tab.setUrl(DRIVER.getCurrentUrl());
        tab.setPurpose(purpose);
        TABS.put(handle, tab);
        return handle;
    }


    private static WebDriver init() {

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

        WebDriver driver = new ChromeDriver(service, cap);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }

    public static void doLogout() {
        DRIVER.quit();
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

    public abstract void doLogin();

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



    public static void switchToFrameContainingCharSeq(String charSeq) {
        DRIVER.switchTo().defaultContent();
        SleepFactory.sleep(2);
        final String frameId= findFrameContainingCharSeq(collectAllFrames(DRIVER), charSeq);
        SleepFactory.sleep(2);
        switch (frameId) {
            case FRAME_ID_DEFAULT_CONTENT:
                DRIVER.switchTo().defaultContent();
                break;
            case FRAME_ID_NONE_FOUND:
                break;
            default:
                DRIVER.switchTo().frame(Integer.parseInt(frameId));
        }
    }

    /**
     * Return the first occurrence of specified class that has specified label.
     */
    public static WebElement getWhere(String className, CRITERIA criteria, String text) {
        switchToFrameContainingCharSeq(text);
        List<WebElement> elements = DRIVER.findElements(By.className(className));
        return filterElementListBy(elements, criteria, text);
    }

    public static WebElement getWhere(String className) {
        switchToFrameContainingCharSeq(className);
        List<WebElement> elements = DRIVER.findElements(By.className(className));
        return filterElementListBy(elements, CRITERIA.NONE, Constants.EMPTY);
    }

    public static void followContainedLink(WebElement element) {
        String link = element.getAttribute("href");
        if (link != null) DRIVER.navigate().to(link);
    }



    public static void setRadio(By by, boolean checked) {
        ((JavascriptExecutor) DRIVER).executeScript("arguments[0].checked = "+ checked + ";", DRIVER.findElement(by));
    }

    public static void setComboByDataValue(By comboBy, String dataValue) {
        WebElement combo = DRIVER.findElement(comboBy);
        combo.click();
        combo.findElement(By.cssSelector("li[data-value='" + dataValue + "']")).click();
    }

    public static WebElement getByFieldValue(String type, String field, String value) {
        return DRIVER.findElement(By.cssSelector(type + "["+ field + "='" + value + "']"));
    }

    public static WebElement getByFieldValue(WebElement element, String type, String field, String value) {
        return element.findElement(By.cssSelector(type + "["+ field + "='" + value + "']"));
    }

    public static WebElement getByText(String text) {
        return DRIVER.findElement (By.xpath ("//*[contains(text(),'" + text + "')]"));
    }

    public static void sendDeleteKeys(WebElement element, int n) {
        for (int i = 0; i < n; i++) {
            element.sendKeys(Keys.BACK_SPACE);
        }
    }



    public static void switchToTab(String handle) {
        for (Entry<String,Tab> entry : TABS.entrySet()) {
            String tabHandle = entry.getKey();
            if (tabHandle.equals(handle)) {
                DRIVER.switchTo().window(tabHandle);
            }
        }
    }

    public static void switchToTab(Tab tab) {
        DRIVER.switchTo().window(tab.getHandle());
    }

    /**
     * Opens a new tab, and "moves" the WebDriver to the new tab.
     * If the current tab is empty, it is registered - this happens usually only with the initial tab of the session.
     */
    public static void newTab(TabPurpose purpose) {
        String currentUrl = DRIVER.getCurrentUrl();
        if (currentUrl.isEmpty()
            || currentUrl.equals("data:,")
            || currentUrl.equals("about:blank")) {
            registerExistingTab(purpose);
        } else {
            execute("window.open(arguments[0])");
            switchToTab(registerBlankTab(purpose));
        }
    }

    public static void execute(String script) {
        System.out.println(script);
        ((JavascriptExecutor) Session.DRIVER).executeScript(script);
    }

    public static void switchToTab(TabPurpose purpose) {
        List<Tab> existingTabs = Session.getTabByPurpose(purpose);
        if (!existingTabs.isEmpty()) {
            Session.switchToTab(existingTabs.get(0));
        } else {
            Tab currentTab = Session.getCurrentTab();
            if (currentTab.getPurpose() == TW_SESSION || currentTab.getPurpose() == SQ_SESSION) {
                currentTab.setPurpose(purpose);
            } else {
                Session.newTab(purpose);
            }
        }
    }

}
