package com.olexyn.abricore.flow.modes;

import com.olexyn.abricore.flow.modes.WebMode;

public abstract class DownloadMode extends WebMode {

    public abstract void downloadHistoricalData() throws InterruptedException;

}
