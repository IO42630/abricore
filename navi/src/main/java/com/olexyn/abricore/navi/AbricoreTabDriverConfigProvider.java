package com.olexyn.abricore.navi;

import com.olexyn.abricore.store.dao.EventDao;
import com.olexyn.abricore.util.CtxAware;
import com.olexyn.propconf.PropConf;
import com.olexyn.tabdriver.TabDriverConfigProvider;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.HashMap;

import static com.olexyn.abricore.model.runtime.EventKeys.IS_HEADLESS;
import static com.olexyn.abricore.util.Constants.WORKING_DIR;


@Lazy
@Service
public class AbricoreTabDriverConfigProvider extends CtxAware implements TabDriverConfigProvider {

    private static final String CHROME_DRIVER = "chromedriver_124";


    @Autowired
    public AbricoreTabDriverConfigProvider(ConfigurableApplicationContext ctx) {
        super(ctx);
    }


    @Override
    public Path getDriverPath() {
        return Path.of(PropConf.get(WORKING_DIR), "/navi/src/main/resources/", CHROME_DRIVER);
    }

    @Override
    public String getDownloadDir() {
        return PropConf.get("quotes.dir.tmp");
    }

    @Override
    public boolean isHeadless() {
        return bean(EventDao.class).getBool(IS_HEADLESS);
    }

    @Override
    public ChromeOptions getOptions() {

        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        options.addArguments("--start-maximized");
        if (isHeadless()) {
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--headless");
        }
        // see also https://chromium.googlesource.com/chromium/src/+/master/chrome/common/pref_names.cc
        HashMap<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.default_content_settings.popups", 0);
        chromePrefs.put("download.default_directory", getDownloadDir());
        chromePrefs.put("download.prompt_for_download", false);
        options.setExperimentalOption("prefs", chromePrefs);
        return options;
    }

}
