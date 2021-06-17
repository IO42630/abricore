package com.olexyn.abricore.flow;

import com.olexyn.abricore.fingers.Fetch.Mode;
import com.olexyn.abricore.fingers.tw.TwFetch;
import com.olexyn.abricore.fingers.tw.TwLogin;
import com.olexyn.abricore.model.Asset;
import org.openqa.selenium.WebDriver;

public abstract class DownloadMode extends WebMode {

    public abstract void downloadHistoricalData() throws InterruptedException;

}
