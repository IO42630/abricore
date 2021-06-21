package com.olexyn.abricore.fingers;

import com.olexyn.abricore.model.Asset;
import com.olexyn.abricore.datastore.Interval;
import com.olexyn.abricore.model.snapshots.AssetSnapshot;

/**
 * Navigates a Webpage. Mostly consists of utility methods and shortcuts.
 */
public interface Navigator {

    AssetSnapshot resolveQuote(Asset asset);

}
