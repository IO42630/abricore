package com.olexyn.abricore.fingers;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.model.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;
import com.olexyn.abricore.session.Session;
import org.openqa.selenium.WebDriver;

/**
 * Navigates a Webpage. Mostly consists of utility methods and shortcuts.
 */
public interface Navigator {

    AssetSnapshot resolveQuote(Asset asset, Interval interval);

}
